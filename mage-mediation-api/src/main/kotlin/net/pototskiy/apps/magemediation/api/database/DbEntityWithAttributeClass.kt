package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.DATABASE_LOG_NAME
import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.ListType
import net.pototskiy.apps.magemediation.api.entity.MapType
import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.api.entity.isList
import net.pototskiy.apps.magemediation.api.entity.isTypeOf
import net.pototskiy.apps.magemediation.api.entity.sqlType
import net.pototskiy.apps.magemediation.api.entity.toList
import net.pototskiy.apps.magemediation.api.entity.values.wrapAValue
import org.apache.commons.collections4.map.LRUMap
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Collections.synchronizedMap
import kotlin.reflect.KClass

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

    protected fun getAttributeClassFor(type: KClass<out Type>): AttributeEntityClass<*, *> {
        val klass = attributeClassCache[type]
        if (klass != null) return klass
        return attributeClasses.find {
            type.sqlType().isInstance((it.table as AttributeTable<*>).value.columnType)
        }?.also { attributeClassCache[type] = it }
            ?: throw DatabaseException("Value of type<${type::class.simpleName}> does not support sql column")
    }

    @PublicApi
    fun readAttribute(entity: DbEntity, attribute: AnyTypeAttribute): Type? {
        if (attribute.isSynthetic) return attribute.builder?.build(entity)
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
        eType.attributes.filter { it.isSynthetic }
            .forEach { entity.data[it] = readAttribute(entity, it) }
        return entity.data
    }

    @PublicApi
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

    @PublicApi
    fun updateAttribute(entity: DbEntity, attribute: AnyTypeAttribute, value: Type) {
        removeAttribute(entity, attribute)
        addAttribute(entity, attribute, value)
    }

    @PublicApi
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

    companion object {
        private const val maxSize = 200
        private const val initialSize = 50
    }
}
