package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.DATABASE_LOG_NAME
import net.pototskiy.apps.magemediation.api.TIMESTAMP
import net.pototskiy.apps.magemediation.api.entity.*
import net.pototskiy.apps.magemediation.api.entity.values.wrapAValue
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.Duration
import kotlin.reflect.KClass

abstract class DbEntityClass(
    vararg attributeClasses: AttributeEntityClass<*, *>
) : IntEntityClass<DbEntity>(DbEntityTable, DbEntity::class.java) {

    val myTable by lazy { super.table as DbEntityTable }
    private val logger = LogManager.getLogger(DATABASE_LOG_NAME)

    @Suppress("MemberVisibilityCanBePrivate")
    protected val attributeClasses: List<AttributeEntityClass<*, *>> =
        attributeClasses.toList()

    fun getEntitiesWithAttributes(etype: EType): List<DbEntity> {
        return getEntities(etype).map {
            readAttributes(it)
            it
        }
    }

    fun getEntities(etype: EType): List<DbEntity> {
        return transaction { find { myTable.entityType eq etype.type }.toList() }
    }

    private fun getAttributeClassFor(type: KClass<out Type>): AttributeEntityClass<*, *> =
        attributeClasses.find {
            type.sqlType().isInstance((it.table as AttributeTable<*>).value.columnType)
        }
            ?: throw DatabaseException("Value of type<${type::class.simpleName}> does not support sql column type or it is transient")


    fun getByAttribute(eType: EType, attribute: AnyTypeAttribute, value: Type): List<DbEntity> =
        getEntitiesByAttributes(eType, mapOf(attribute to value))

    fun readAttribute(entity: DbEntity, attribute: AnyTypeAttribute): Type? {
        if (attribute.isSynthetic) {
            return attribute.builder?.build(entity)
        }
        entity.eType.checkAttributeDefined(attribute)
        val attrClass = getAttributeClassFor(attribute.valueType)
        val attrTable = attrClass.table as AttributeTable<*>
        return transaction {
            val value = attrTable
                .slice(attrTable.index, attrTable.value)
                .select { (attrTable.owner eq entity.id) and (attrTable.code eq attribute.name.attributeName) }
                .map { it[attrTable.index] to it[attrTable.value] }
                .toMap()
            @Suppress("USELESS_CAST")
            val v = wrapAValue(
                attribute, when {
                    value.isEmpty() -> null as Any?
                    value.size > 1 || !value.containsKey(-1) -> value.values.toList()
                    else -> value[-1]
                }
            )
            entity.data[attribute] = v
            v
        }
    }

    fun readAttributes(entity: DbEntity): Map<AnyTypeAttribute, Type?> {
        val etype = entity.eType
        val types = etype.attributes
            .filterNot { it.isSynthetic || it.valueType == AttributeListType::class }
            .groupBy { it.valueType.sqlType() }.keys
        val dbValues = attributeClasses.filter {
            (it.table as AttributeTable<*>).value.columnType::class in types
        }.map { attrClass ->
            val table = attrClass.table as AttributeTable<*>
            val v = transaction { attrClass.find { table.owner eq entity.id }.toList() }
                .groupBy { it.code }
                .map { it.key to it.value }
            v
        }.flatten().toMap()
        @Suppress("IMPLICIT_CAST_TO_ANY")
        val v = etype.attributes.map { attr ->
            attr to wrapAValue(
                attr,
                dbValues[attr.name.attributeName]?.let { valueList ->
                    val value = valueList.map { it.index to it.value }.toMap()
                    when {
                        value.isEmpty() -> null
                        value.size > 1 || !value.containsKey(-1) -> value.values.toList()
                        else -> value[-1]
                    }
                }
            )
        }.toMap()
        entity.data.clear()
        entity.data.putAll(v)
        etype.attributes.filter { it.isSynthetic }
            .forEach { entity.data[it] = readAttribute(entity, it) }
        return entity.data
    }

    fun addAttribute(entity: DbEntity, attribute: AnyTypeAttribute, value: Type) {
        val eType = entity.eType
        eType.checkAttributeDefined(attribute)
        val attrClass = getAttributeClassFor(attribute.valueType)
        if (value.isTypeOf<MapType<*, *>>()) {
            throw DatabaseException("MapType is not supported for persistent attribute")
        }
        (if (!value.isTypeOf<ListType<*>>()) value.toList() else (value as ListType<*>))
            .forEachIndexed { position, data ->
                when (data) {
                    null -> logger.error("Null value can not be assigned to attribute, attribute should be removed")
                    !is Type -> logger.error("Only value of type Type is supported")
                    else -> writeAttributeToDb(attrClass, entity, attribute, position, data)
                }
            }
    }

    private fun writeAttributeToDb(
        attrClass: AttributeEntityClass<*, *>,
        entity: DbEntity,
        attribute: AnyTypeAttribute,
        position: Int,
        data: Type
    ) {
        transaction {
            attrClass.new {
                this.owner = entity.id
                this.code = attribute.name.attributeName
                this.index = if (attribute.valueType.isList()) position else -1
                try {
                    this.setValueWithTypeCheck(data)
                } catch (e: DatabaseException) {
                    throw DatabaseException(
                        "Value can not be assigned to attribute<${attribute.name}>, types are incompatible",
                        e
                    )
                }
            }
        }
    }

    fun updateAttribute(entity: DbEntity, attribute: AnyTypeAttribute, value: Type) {
        removeAttribute(entity, attribute)
        addAttribute(entity, attribute, value)
    }

    fun removeAttribute(entity: DbEntity, attribute: AnyTypeAttribute) {
        entity.eType.checkAttributeDefined(attribute)
        val attrClass = getAttributeClassFor(attribute.valueType)
        val attrTable = attrClass.table as AttributeTable<*>
        transaction {
            attrTable.deleteWhere {
                (attrTable.owner eq entity.id) and (attrTable.code eq attribute.name.attributeName)
            }
        }
    }

    fun getEntityByKeys(eType: EType, keys: Map<AnyTypeAttribute, Type?>): DbEntity? =
        getEntitiesByAttributes(eType, keys.filter { it.key.key }).firstOrNull()

    fun getEntitiesByAttributes(
        eType: EType,
        data: Map<AnyTypeAttribute, Type?>
    ): List<DbEntity> = transaction {
        transaction {
            var from: ColumnSet = myTable
            var where = Op.build { myTable.entityType eq eType.type }
            data.filterNot { it.value == null }.forEach { attr, value ->
                value as Type
                val attrClass = getAttributeClassFor(attr.valueType)
                val attrTable = attrClass.table as AttributeTable<*>
                val alias = attrTable.alias("${attr.name.attributeName}_table")
                from = from.innerJoin(alias, { table.id }, { alias[attrTable.owner] })
                where = where.and(Op.build { alias[attrTable.code] eq attr.name.attributeName })
                where = when (attr.valueType.sqlType()) {
                    VarCharColumnType::class ->
                        where.and(Op.build { aliasColumn<String>(alias, attrTable.value) eq (value.value as String) })
                    LongColumnType::class ->
                        where.and(Op.build { aliasColumn<Long>(alias, attrTable.value) eq (value.value as Long) })
                    DoubleColumnType::class ->
                        where.and(Op.build { aliasColumn<Double>(alias, attrTable.value) eq (value.value as Double) })
                    BooleanColumnType::class ->
                        where.and(Op.build { aliasColumn<Boolean>(alias, attrTable.value) eq (value.value as Boolean) })
                    DateColumnType::class ->
                        where.and(Op.build {
                            aliasColumn<DateTime>(
                                alias,
                                attrTable.value
                            ) eq (value.value as DateTime)
                        })
                    else ->
                        throw DatabaseException("Field<${attr.name}> with type<${attr.valueType.simpleName}> can not be key field")
                }
            }
            from
                .slice(table.columns)
                .select { where }
                .map { wrapRow(it) }
                .toList()
        }
    }

    fun insertEntity(etype: EType, data: Map<AnyTypeAttribute, Type>): DbEntity {
        return transaction {
            val entity =
                new {
                    entityType = etype.type
                    touchedInLoading = true
                    previousStatus = EntityStatus.CREATED
                    currentStatus = EntityStatus.CREATED
                    created = TIMESTAMP
                    updated = TIMESTAMP
                    absentDays = 0
                }
            data.filterNot { it.key.isSynthetic || it.value.isTransient }
                .forEach { attribute, value -> addAttribute(entity, attribute, value) }
            entity
        }
    }

    fun resetTouchFlag(etype: EType) {
        transaction {
            table.update({ getClassWhereClause(etype) }) {
                it[myTable.touchedInLoading] = false
            }
        }
    }

    private fun getClassWhereClause(eType: EType): Op<Boolean> = Op.build {
        myTable.entityType eq eType.type
    }

    fun markEntitiesAsRemove(etype: EType) {
        transaction {
            table.update({
                getClassWhereClause(etype)
                    .and(myTable.touchedInLoading eq false)
                    .and(myTable.currentStatus neq EntityStatus.REMOVED)
            }
            ) {
                it.update(myTable.previousStatus, myTable.currentStatus)
                it[myTable.currentStatus] = EntityStatus.REMOVED
                it[myTable.removed] = TIMESTAMP
            }
        }
    }

    fun removeOldEntities(eType: EType, maxAge: Int) {
        transaction {
            find {
                getClassWhereClause(eType).and(
                    (myTable.absentDays greaterEq maxAge)
                            and (myTable.currentStatus eq EntityStatus.REMOVED)
                )
            }.toList()
        }.forEach {
            transaction { it.delete() }
        }
    }

    fun updateAbsentAge(etype: EType) {
        transaction {
            find {
                getClassWhereClause(etype).and(myTable.currentStatus eq EntityStatus.REMOVED)
            }.toList()
        }.forEach {
            val days = Duration(it.removed, TIMESTAMP).standardDays.toInt()
            transaction { it.absentDays = days }
        }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> aliasColumn(alias: Alias<AttributeTable<*>>, column: Column<*>) = (alias[column] as Column<T>)

