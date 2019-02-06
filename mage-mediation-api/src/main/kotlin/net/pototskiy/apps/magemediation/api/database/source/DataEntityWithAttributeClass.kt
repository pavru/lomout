package net.pototskiy.apps.magemediation.api.database.source

import net.pototskiy.apps.magemediation.api.DATABASE_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.type.*
import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.api.database.createAttributeDescription
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.reflect.KClass

abstract class DataEntityWithAttributeClass<out E : DataEntityWithAttribute<*>>(
    table: IntIdTable,
    entityClass: Class<E>? = null,
    vararg attrEntityClass: TypedAttributeEntityClass<*, *>
) : IntEntityClass<E>(table, entityClass) {

    @Suppress("UNCHECKED_CAST")
    protected val entityClass: Class<*> = entityClass ?: javaClass.enclosingClass as Class<E>

    private val logger = LogManager.getLogger(DATABASE_LOG_NAME)

    @Suppress("MemberVisibilityCanBePrivate")
    protected val attributes: List<TypedAttributeEntityClass<*, *>> = attrEntityClass.toList()

    fun getAttrEntityClassFor(type: AttributeType): TypedAttributeEntityClass<*, *> =
        when (type) {
            is AttributeStringType,
            is AttributeStringListType -> findNonDateAttrEntityClass(VarCharColumnType::class)
            is AttributeLongType,
            is AttributeIntListType -> findNonDateAttrEntityClass(LongColumnType::class)
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
        } ?: throw DatabaseException("Entity class<${entityClass.simpleName}> does not support attributeEntityClasses with type<${type::class.simpleName}>")


    private fun findDateTimeAttrEntityClass(): TypedAttributeEntityClass<*, *>? {
        return attributes.findLast { attr ->
            val attrTable = attr.table as TypedAttributeTable<*>
            val valueColumnType = attrTable.value.columnType
            valueColumnType is DateColumnType && valueColumnType.time
        }
    }

    private fun findDateAttrEntityClass(): TypedAttributeEntityClass<*, *>? {
        return attributes.findLast { attr ->
            val attrTable = attr.table as TypedAttributeTable<*>
            val valueColumnType = attrTable.value.columnType
            valueColumnType is DateColumnType && !valueColumnType.time
        }
    }

    private fun findNonDateAttrEntityClass(type: KClass<out ColumnType>): TypedAttributeEntityClass<*, *>? {
        return attributes.findLast { attr ->
            val attrTable = attr.table as TypedAttributeTable<*>
            val valueColumnType = attrTable.value.columnType
            type.isInstance(valueColumnType)
        }
    }

    fun findByAttribute(attribute: Attribute, value: Any): List<E> {
        if (attribute.isSynthetic) return emptyList()
        val attrClass = getAttrEntityClassFor(attribute.type)
        val attrTable = attrClass.table as TypedAttributeTable<*>
        return transaction {
            (table innerJoin attrTable)
                .slice(table.columns)
                .select {
                    ((attrTable.code eq attribute.name) and equalExpression(attrTable.value, value))
                }
                .groupBy(*table.columns.toTypedArray())
                .map { wrapRow(it) }
        }
    }

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

    fun getAttribute(entity: DataEntityWithAttribute<*>, attribute: Attribute): Any? {
        if (attribute.isSynthetic) {
            return null//attribute.builder?.build(entity)
        }
        val attrClass = getAttrEntityClassFor(attribute.type)
        val attrTable = attrClass.table as TypedAttributeTable<*>
        return transaction {
            val value = (table innerJoin attrTable)
                .slice(attrTable.value)
                .select { (attrTable.owner eq entity.id) and (attrTable.code eq attribute.name) }
                .map { it[attrTable.value] }
                .toList()
            if (value.isEmpty()) null else if (value.size > 1) value else value.first()
        }
    }

    fun getAttributes(entity: DataEntityWithAttribute<*>): Map<Attribute, Any> {
        return attributes.map { attrClass ->
            val attrTable = attrClass.table as TypedAttributeTable<*>
            transaction {
                val attrCodes = attrTable
                    .slice(attrTable.code)
                    .select { attrTable.owner eq entity.id }
                    .groupBy(attrTable.code)
                    .map { it[attrTable.code] }
                attrCodes.map { attrCode ->
                    val value = attrClass.find {
                        (attrTable.owner eq entity.id) and (attrTable.code eq attrCode)
                    }.toList()
                    val returnValue: Any? = when {
                        value.isEmpty() -> null
                        value.first().index == -1 -> value.first().value
                        else -> value.map { it.value }
                    }
                    if (returnValue == null) {
                        null
                    } else {
                        attrClass.createAttributeDescription(attrCode, returnValue) to returnValue
                    }
                }
            }
        }
            .flatten()
            .filterNotNull()
            .toMap()
    }

    fun addAttribute(entity: DataEntityWithAttribute<*>, attribute: Attribute, value: Any) {
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

    fun updateAttribute(entity: DataEntityWithAttribute<*>, attribute: Attribute, value: Any) {
        removeAttribute(entity, attribute)
        addAttribute(entity, attribute, value)
    }

    fun removeAttribute(entity: DataEntityWithAttribute<*>, attribute: Attribute) {
        val attrClass = getAttrEntityClassFor(attribute.type)
        val attrTable = attrClass.table as TypedAttributeTable<*>
        transaction {
            attrTable.deleteWhere {
                (attrTable.owner eq entity.id) and (attrTable.code eq attribute.name)
            }
        }
    }

    fun findEntityByKeyFields(data: Map<Attribute, Any?>): E? = transaction {
        transaction {
            var from: ColumnSet = table
            var where = Op.build { table.id.isNotNull() }
            data.filter { it.key.key }.forEach { field, value ->
                val attrClass = getAttrEntityClassFor(field.type)
                val attrTable = attrClass.table as TypedAttributeTable<*>
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
                .firstOrNull()
        }
    }

}
