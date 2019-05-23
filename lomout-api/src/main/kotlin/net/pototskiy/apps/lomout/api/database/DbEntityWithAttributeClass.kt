package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.AppDatabaseException
import net.pototskiy.apps.lomout.api.DATABASE_LOG_NAME
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.AttributeListType
import net.pototskiy.apps.lomout.api.entity.ListType
import net.pototskiy.apps.lomout.api.entity.MapType
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.entity.isList
import net.pototskiy.apps.lomout.api.entity.isTypeOf
import net.pototskiy.apps.lomout.api.entity.sqlType
import net.pototskiy.apps.lomout.api.entity.toList
import net.pototskiy.apps.lomout.api.entity.values.wrapAValue
import org.apache.commons.collections4.map.LRUMap
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Collections.*
import kotlin.collections.set
import kotlin.reflect.KClass

/**
 * Exposed entity class for domain entity with attributes
 *
 * @constructor
 * @param attributeClasses AttributeEntityClass<*, *> Exposed entity class for attributes
 */
abstract class DbEntityWithAttributeClass(
    vararg attributeClasses: AttributeEntityClass<*, *>
) : IntEntityClass<DbEntity>(DbEntityTable, DbEntity::class.java) {

    private val attributeClassCache = synchronizedMap(
        LRUMap<KClass<out Type>, AttributeEntityClass<*, *>>(
            maxSize,
            initialSize
        )
    )
    private val myTable by lazy { super.table as DbEntityTable }
    private val logger = LogManager.getLogger(DATABASE_LOG_NAME)
    private val attributeClasses: List<AttributeEntityClass<*, *>> =
        attributeClasses.toList()

    /**
     * Get exposed entity class for attribute type
     *
     * @param type KClass<out Type> The attribute type
     * @return AttributeEntityClass<*, *>
     */
    protected fun getAttributeClassFor(type: KClass<out Type>): AttributeEntityClass<*, *> {
        val klass = attributeClassCache[type]
        if (klass != null) return klass
        return attributeClasses.find {
            type.sqlType().isInstance((it.table as AttributeTable<*>).value.columnType)
        }?.also { attributeClassCache[type] = it }
            ?: throw AppDatabaseException("Value of type<${type::class.simpleName}> does not support sql column")
    }

    /**
     * Read DB entity attribute
     *
     * @param entity DbEntity The DB entity
     * @param attribute AnyTypeAttribute The attribute
     * @return Type?
     */
    @PublicApi
    fun readAttribute(entity: DbEntity, attribute: AnyTypeAttribute): Type? {
        if (attribute.isSynthetic) return attribute.builder?.build(entity)
        entity.eType.checkAttributeDefined(attribute)
        val attrClass = getAttributeClassFor(attribute.valueType)
        val attrTable = attrClass.table as AttributeTable<*>
        return transaction {
            val value = attrTable
                .slice(attrTable.index, attrTable.value)
                .select { (attrTable.owner eq entity.id) and (attrTable.code eq attribute.name) }
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

    /**
     * Read all DB entity attributes
     *
     * @param entity DbEntity The DB entity
     * @return Map<AnyTypeAttribute, Type?>
     */
    @PublicApi
    fun readAttributes(entity: DbEntity): Map<AnyTypeAttribute, Type?> {
        val eType = entity.eType
        val types = eType.attributes
            .filterNot { it.isSynthetic || it.valueType.isTypeOf<AttributeListType>() }
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
        val v = eType.attributes.map { attr ->
            attr to wrapAValue(
                attr,
                dbValues[attr.name]?.let { valueList ->
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
        eType.attributes.filter { it.isSynthetic }
            .forEach { entity.data[it] = readAttribute(entity, it) }
        return entity.data
    }

    /**
     * Add the attribute to DB entity
     *
     * @param entity The DB entity
     * @param attribute The attribute
     * @param value The attribute value
     */
    @PublicApi
    fun addAttribute(entity: DbEntity, attribute: AnyTypeAttribute, value: Type) {
        val eType = entity.eType
        eType.checkAttributeDefined(attribute)
        val attrClass = getAttributeClassFor(attribute.valueType)
        if (value.isTypeOf<MapType<*, *>>()) {
            throw AppDatabaseException("MapType is not supported for the persistent attribute")
        }
        (if (!value.isTypeOf<ListType<*>>()) value.toList() else (value as ListType<*>))
            .forEachIndexed { position, data ->
                when (data) {
                    null -> logger.error("Null value cannot be assigned to attribute, attribute should be removed")
                    !is Type -> logger.error("Value supports only Type class")
                    else -> writeAttributeToDb(attrClass, entity, attribute, position, data)
                }
            }
    }

    /**
     * Update entity attribute value
     *
     * @param entity DbEntity The DB entity
     * @param attribute AnyTypeAttribute The attribute
     * @param value Type
     */
    @PublicApi
    fun updateAttribute(entity: DbEntity, attribute: AnyTypeAttribute, value: Type) {
        removeAttribute(entity, attribute)
        addAttribute(entity, attribute, value)
    }

    /**
     * Remove entity attribute
     *
     * @param entity DbEntity The DB entity
     * @param attribute AnyTypeAttribute the attribute
     */
    @PublicApi
    fun removeAttribute(entity: DbEntity, attribute: AnyTypeAttribute) {
        entity.eType.checkAttributeDefined(attribute)
        val attrClass = getAttributeClassFor(attribute.valueType)
        val attrTable = attrClass.table as AttributeTable<*>
        transaction {
            attrTable.deleteWhere {
                (attrTable.owner eq entity.id) and (attrTable.code eq attribute.name)
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
                this.code = attribute.name
                this.index = if (attribute.valueType.isList()) position else -1
                try {
                    this.setValueWithTypeCheck(data)
                } catch (e: AppDatabaseException) {
                    throw AppDatabaseException(
                        "Value cannot be assigned to attribute<${attribute.name}>, types are incompatible",
                        e
                    )
                }
            }
        }
    }

    /**
     * Companion object
     */
    companion object {
        private const val maxSize = 200
        private const val initialSize = 50
    }
}
