package net.pototskiy.apps.magemediation.api.database

import net.pototskiy.apps.magemediation.api.config.type.*
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.*

abstract class TypedAttributeEntityClass<V : Comparable<V>, out E : TypedAttributeEntity<V>>(
    table: TypedAttributeTable<V>,
    entityClass: Class<E>? = null
) : IntEntityClass<E>(table, entityClass)

fun TypedAttributeEntityClass<*, *>.createAttributeDescription(name: String, value: Any): Attribute {
    val type = (table as TypedAttributeTable<*>).value.columnType
    return Attribute(
        name,
        when {
            type is VarCharColumnType && value !is List<*> -> AttributeStringType(false)
            type is VarCharColumnType && value is List<*> -> AttributeStringListType("\"", ",")
            type is TextColumnType && value !is List<*> -> AttributeTextType(false)
            type is BooleanColumnType && value !is List<*> -> AttributeBoolType()
            type is BooleanColumnType && value is List<*> -> AttributeBoolListType("\"", ",")
            type is LongColumnType && value !is List<*> -> AttributeLongType(false)
            type is LongColumnType && value is List<*> -> AttributeIntListType("\"", ",", false)
            type is DoubleColumnType && value !is List<*> -> AttributeDoubleType(false)
            type is DoubleColumnType && value is List<*> -> AttributeDoubleListType("\"", ",", false)
            type is DateColumnType && !type.time && value !is List<*> ->
                AttributeDateType(false, "", false)
            type is DateColumnType && !type.time && value is List<*> ->
                AttributeDateListType("\"", ",", false, "", false)
            type is DateColumnType && type.time && value !is List<*> ->
                AttributeDateTimeType(false, "", false)
            type is DateColumnType && type.time && value is List<*> ->
                AttributeDateTimeListType("\"", ",", false, "", false)
            else -> throw DatabaseException("Attribute description can not be build for ${this::class.simpleName}")
        },
        false,
        false
        , true,
        null
    )
}
