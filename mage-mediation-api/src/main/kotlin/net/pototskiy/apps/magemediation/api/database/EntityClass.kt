package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

class EntityClass<out E : PersistentEntity<*>>(
    val type: String,
    val backend: PersistentEntityClass<E>,
    private val declaredAttributes: List<Attribute>,
    val open: Boolean
) {
    private var generatedAttributes: MutableList<Attribute> = mutableListOf()
    val attributes
        get() = declaredAttributes.plus(generatedAttributes)

    fun refineGenerateAttributes(attrs: List<Attribute>) {
        if (open) {
            generatedAttributes.addAll(attrs.minus(generatedAttributes))
        } else {
            throw ConfigException("Entity<$type> has final type and therefore attributes can not be refined")
        }
    }

    fun mapAttribute(attr: Attribute) = attributes.find { it.name == attr.name }
        ?: throw DatabaseException("Attribute<${attr.name}> is not defined for entity<$type>")

    fun getEntitiesWithAttributes(): List<E> =
        backend.getEntitiesWithAttributes(this)

    fun getEntities(): List<E> =
        backend.getEntities(this)

    private fun isAttributeDefined(attribute: Attribute) = attributes.any { it.name == attribute.name }

    fun checkAttributeDefined(attribute: Attribute) {
        if (!isAttributeDefined(attribute)) {
            throw DatabaseException("Attribute<${attribute.name}> is not defined for entity<$type>")
        }
    }

    fun getByAttribute(attribute: Attribute, value: Any): List<E> =
        getEntitiesByAttributes(mapOf(attribute to value))

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

    fun readAttribute(entity: PersistentEntity<*>, attribute: Attribute): Any? =
        backend.readAttribute(entity, attribute)

    fun readAttributes(entity: PersistentEntity<*>): Map<Attribute, Any?> =
        backend.readAttributes(entity)

    fun getEntityByKeys(keys: Map<Attribute, Any?>): E? =
        backend.getEntityByKeys(this, keys)

    @PublicApi
    fun getEntitiesByAttributes(data: Map<Attribute, Any?>): List<E> =
        backend.getEntitiesByAttributes(this, data)


    companion object {
        private val classRegister = mutableMapOf<String, EntityClass<*>>()

        fun initEntityCLassRegistrar() = classRegister.clear()

        fun registerClass(clazz: EntityClass<*>) =
            classRegister.replace(clazz.type, clazz) ?: classRegister.put(clazz.type, clazz)

        fun getClass(type: String): EntityClass<*>? = classRegister[type]
        fun getOrRegisterClass(clazz: EntityClass<*>) =
            classRegister[clazz.type] ?: (clazz.also {
                registerClass(
                    clazz
                )
            })
    }
}
