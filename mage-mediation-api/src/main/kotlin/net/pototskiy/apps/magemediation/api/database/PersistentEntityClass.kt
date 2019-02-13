package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.DATABASE_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.data.*
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.reflect.KClass

abstract class PersistentEntityClass<out E : PersistentEntity<*>>(
    table: IntIdTable,
    entityClass: Class<E>? = null,
    vararg attrEntityClass: AttributeEntityClass<*, *>
) : IntEntityClass<E>(table, entityClass) {

    @Suppress("UNCHECKED_CAST")
    protected val entityClass: Class<*> = entityClass ?: javaClass.enclosingClass as Class<E>

    private val logger = LogManager.getLogger(DATABASE_LOG_NAME)

    @Suppress("MemberVisibilityCanBePrivate")
    protected val attributeEntityClasses: List<AttributeEntityClass<*, *>> = attrEntityClass.toList()

    fun getEntitiesWithAttributes(entityClass: EntityClass<*>): List<E> {
        return getEntities(entityClass).map {
            readAttributes(it)
            it
        }
    }

    fun getEntities(entityClass: EntityClass<*>): List<E> {
        table as PersistentEntityTable
        return transaction { find { table.entityType eq entityClass.type }.toList() }
    }

    private fun Map<Attribute, Any?>.mapAttributeDescription(entityClass: EntityClass<*>) =
        this.map { (attr, value) -> entityClass.mapAttribute(attr) to value }.toMap()

    private fun getAttrEntityClassFor(type: AttributeType): AttributeEntityClass<*, *> =
        when (type) {
            is AttributeStringType,
            is AttributeStringListType -> findNonDateAttrEntityClass(VarCharColumnType::class)
            is AttributeLongType,
            is AttributeLongListType -> findNonDateAttrEntityClass(LongColumnType::class)
            is AttributeDoubleType,
            is AttributeDoubleListType -> findNonDateAttrEntityClass(DoubleColumnType::class)
            is AttributeBoolType,
            is AttributeBoolListType -> findNonDateAttrEntityClass(BooleanColumnType::class)
            is AttributeTextType -> findNonDateAttrEntityClass(TextColumnType::class)
            is AttributeDateType,
            is AttributeDateListType -> findDateAttrEntityClass()
            is AttributeDateTimeType,
            is AttributeDateTimeListType -> findDateTimeAttrEntityClass()
            is AttributeAttributeListType -> throw DatabaseException("Attribute list can not have special store")
        }
            ?: throw DatabaseException("Entity class<${entityClass.simpleName}> does not support attributeEntityClasses with type<${type::class.simpleName}>")


    private fun findDateTimeAttrEntityClass(): AttributeEntityClass<*, *>? {
        return attributeEntityClasses.findLast { attr ->
            val attrTable = attr.table as AttributeTable<*>
            val valueColumnType = attrTable.value.columnType
            valueColumnType is DateColumnType && valueColumnType.time
        }
    }

    private fun findDateAttrEntityClass(): AttributeEntityClass<*, *>? {
        return attributeEntityClasses.findLast { attr ->
            val attrTable = attr.table as AttributeTable<*>
            val valueColumnType = attrTable.value.columnType
            valueColumnType is DateColumnType && !valueColumnType.time
        }
    }

    private fun findNonDateAttrEntityClass(type: KClass<out ColumnType>): AttributeEntityClass<*, *>? {
        return attributeEntityClasses.findLast { attr ->
            val attrTable = attr.table as AttributeTable<*>
            val valueColumnType = attrTable.value.columnType
            type.isInstance(valueColumnType)
        }
    }

    fun getByAttribute(entityClass: EntityClass<*>, attribute: Attribute, value: Any): List<E> =
        getEntitiesByAttributes(entityClass, mapOf(attribute to value))

    @Suppress("UNCHECKED_CAST")
    private fun SqlExpressionBuilder.equalExpression(c: Column<*>, value: Any): Op<Boolean> {
        return when {
            c.columnType is VarCharColumnType -> (c as Column<String>) eq value.toString()
            c.columnType is LongColumnType -> (c as Column<Long>) eq value.castToLong()
            c.columnType is DoubleColumnType -> (c as Column<Double>) eq value.castToDouble()
            c.columnType is DateColumnType -> (c as Column<DateTime>) eq value.castToDateTime()
            c.columnType is BooleanColumnType -> (c as Column<Boolean>) eq value.castToBoolean()
            c.columnType is TextColumnType -> (c as Column<String>) eq value.toString()
            else -> throw DatabaseException("Column and value types are incompatible therefore equal expression can not be built")
        }
    }

    fun readAttribute(entity: PersistentEntity<*>, attribute: Attribute): Any? {
        if (attribute.isSynthetic) {
            return attribute.builder?.build(entity)
        }
        val entityClass = entity.getEntityClass()
        entityClass.checkAttributeDefined(attribute)
        val attrClass = getAttrEntityClassFor(attribute.type)
        val attrTable = attrClass.table as AttributeTable<*>
        return transaction {
            //            Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
//            val value = (table innerJoin attrTable)
            val value = attrTable
                .slice(attrTable.index, attrTable.value)
                .select { (attrTable.owner eq entity.id) and (attrTable.code eq attribute.name) }
                .map { it[attrTable.index] to it[attrTable.value] }
                .toMap()
            @Suppress("USELESS_CAST") val v = when {
                value.isEmpty() -> null as Any?
                value.size > 1 || !value.containsKey(-1) -> value.values.toList()
                else -> value[-1]
            }
            entity.data[entityClass.mapAttribute(attribute)] = v
//            Configurator.setLevel(EXPOSED_LOG_NAME, Level.ERROR)
            v
        }
    }

    fun readAttributes(entity: PersistentEntity<*>): Map<Attribute, Any?> {
//        Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
        val entityClassDef = entity.getEntityClass()
        val types = entityClassDef.attributes.groupBy { it.type.sqlType }.keys.filterNotNull()
        @Suppress("UNCHECKED_CAST")
        val dbValues = attributeEntityClasses.filter {
            (it.table as AttributeTable<*>).value.columnType::class in types
        }
            .map { attrClass ->
                val table = attrClass.table as AttributeTable<*>
                val v = transaction { attrClass.find { table.owner eq entity.id }.toList() }
                    .groupBy { it.code }
                    .map { it.key to it.value }
                v
            }
            .flatten()
            .toMap()
//        Configurator.setLevel(EXPOSED_LOG_NAME, Level.ERROR)
        @Suppress("IMPLICIT_CAST_TO_ANY")
        val v = entityClassDef.attributes.map { attr ->
            attr to
                    dbValues[attr.name]?.let { valueList ->
                        val value = valueList.map { it.index to it.value }.toMap()
                        when {
                            value.isEmpty() -> null
                            value.size > 1 || !value.containsKey(-1) -> value.values.toList()
                            else -> value[-1]
                        }
                    }
        }.toMap()
        entity.data.clear()
        entity.data.putAll(v)
        entityClassDef.attributes.filter { it.isSynthetic }
            .forEach { entity.data[it] = readAttribute(entity, it) }
        return entity.data
    }

    fun addAttribute(entity: PersistentEntity<*>, attribute: Attribute, value: Any) {
        val entityClass = entity.getEntityClass()
        entityClass.checkAttributeDefined(attribute)
        val attrClass = getAttrEntityClassFor(attribute.type)
        (if (value !is List<*>) listOf(value) else value)
            .forEachIndexed { position, data ->
                if (data == null) {
                    logger.error("Null value can not be assigned to attribute, attribute should be removed")
                } else {
                    transaction {
                        attrClass.new {
                            this.owner = entity.id
                            this.code = attribute.name
                            this.index = if (attribute.type.isList) position else -1
                            this.setValueWithTypeCheck(data)
                        }
                    }
                }
            }
    }

    fun updateAttribute(entity: PersistentEntity<*>, attribute: Attribute, value: Any) {
        removeAttribute(entity, attribute)
        addAttribute(entity, attribute, value)
    }

    fun removeAttribute(entity: PersistentEntity<*>, attribute: Attribute) {
        val entityClass = entity.getEntityClass()
        entityClass.checkAttributeDefined(attribute)
        val attrClass = getAttrEntityClassFor(attribute.type)
        val attrTable = attrClass.table as AttributeTable<*>
        transaction {
            attrTable.deleteWhere {
                (attrTable.owner eq entity.id) and (attrTable.code eq attribute.name)
            }
        }
    }

    fun getEntityByKeys(entityClass: EntityClass<*>, keys: Map<Attribute, Any?>): E? =
        getEntitiesByAttributes(entityClass, keys.filter { it.key.key }).firstOrNull()

    fun getEntitiesByAttributes(entityClass: EntityClass<*>, data: Map<Attribute, Any?>): List<E> = transaction {
        transaction {
            var from: ColumnSet = table as PersistentEntityTable
            var where = Op.build { table.entityType eq entityClass.type }
            data.forEach { field, value ->
                val attrClass = getAttrEntityClassFor(field.type)
                val attrTable = attrClass.table as AttributeTable<*>
                val alias = attrTable.alias("${field.name}_table")
                from = from.innerJoin(alias, { table.id }, { alias[attrTable.owner] })
                where = where.and(Op.build { alias[attrTable.code] eq field.name })
                @Suppress("UNCHECKED_CAST")
                where = when (field.type) {
                    is AttributeTextType,
                    is AttributeStringType -> where.and(Op.build { alias[attrTable.value as Column<String>] eq (value as String) })
                    is AttributeLongType -> where.and(Op.build { alias[attrTable.value as Column<Long>] eq (value as Long) })
                    is AttributeDoubleType -> where.and(Op.build { alias[attrTable.value as Column<Double>] eq (value as Double) })
                    is AttributeBoolType -> where.and(Op.build { alias[attrTable.value as Column<Boolean>] eq (value as Boolean) })
                    is AttributeDateTimeType,
                    is AttributeDateType -> where.and(Op.build { alias[attrTable.value as Column<DateTime>] eq (value as DateTime) })
                    else -> throw DatabaseException("Field<${field.name}> with type<${field.type::class.simpleName}> can not be key field")
                }
            }
            from
                .slice(table.columns)
                .select { where }
                .map { wrapRow(it) }
                .toList()
        }
    }
}
