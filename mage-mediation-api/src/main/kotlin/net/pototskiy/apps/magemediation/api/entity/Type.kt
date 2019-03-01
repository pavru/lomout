package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.entity.Type.Companion.TYPE_NOT_SUPPORT_SQL
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import org.joda.time.ReadableDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

sealed class Type {
    abstract val value: Any
    abstract val isTransient: Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Type) return false
        if (other.value != this.value) return false

        return true
    }

    override fun hashCode(): Int {
        return this.value.hashCode()
    }

    fun sqlType(): KClass<out IColumnType> {
        if (isTransient) {
            throw DatabaseException(TYPE_NOT_SUPPORT_SQL)
        }
        return when (this) {
            is BooleanListType -> BooleanColumnType::class
            is LongListType -> LongColumnType::class
            is DoubleListType -> DoubleColumnType::class
            is StringListType -> VarCharColumnType::class
            is TextListType -> TextColumnType::class
            is DateListType -> DateColumnType::class
            is DateTimeListType -> DateColumnType::class
            is AttributeListType -> throw DatabaseException(TYPE_NOT_SUPPORT_SQL)
            is BooleanType -> BooleanColumnType::class
            is LongType -> LongColumnType::class
            is DoubleType -> DoubleColumnType::class
            is StringType -> VarCharColumnType::class
            is DateType -> DateColumnType::class
            is DateTimeType -> DateColumnType::class
            is TextType -> TextColumnType::class
        }
    }

    fun isList() = this is ListType<*>
    fun isMap() = this is MapType<*, *>
    fun isSingle() = !(this is ListType<*> || this is MapType<*, *>)
    inline fun <reified T : Type> isTypeOf() = this is T

    companion object {
        const val TYPE_NOT_SUPPORT_SQL = "Type does not support sql column type"
    }
}

@PublicApi
fun KClass<out Type>.isList(): Boolean = this.isSubclassOf(ListType::class)
@PublicApi
fun KClass<out Type>.isMap(): Boolean = this.isSubclassOf(MapType::class)
@PublicApi
fun KClass<out Type>.isSingle(): Boolean = !(this.isSubclassOf(ListType::class) || this.isSubclassOf(MapType::class))

inline fun <reified T : Type> KClass<out Type>.isTypeOf(): Boolean {
    return this.isSubclassOf(T::class)
}

fun KClass<out Type>.sqlType(): KClass<out IColumnType> = when (this) {
    BooleanListType::class -> BooleanColumnType::class
    LongListType::class -> LongColumnType::class
    DoubleListType::class -> DoubleColumnType::class
    StringListType::class -> VarCharColumnType::class
    DateListType::class -> DateColumnType::class
    DateTimeListType::class -> DateColumnType::class
    AttributeListType::class -> throw DatabaseException(TYPE_NOT_SUPPORT_SQL)
    BooleanType::class -> BooleanColumnType::class
    LongType::class -> LongColumnType::class
    DoubleType::class -> DoubleColumnType::class
    StringType::class -> VarCharColumnType::class
    DateType::class -> DateColumnType::class
    DateTimeType::class -> DateColumnType::class
    TextType::class -> TextColumnType::class
    TextListType::class -> TextColumnType::class
    else -> throw DatabaseException(TYPE_NOT_SUPPORT_SQL)
}


sealed class ListType<T>(override val value: List<T>, override val isTransient: Boolean = false) :
    Type(), List<T> by value {
    override fun toString(): String = value.toString()
}

sealed class MapType<K, V>(override val value: Map<K, V>, override val isTransient: Boolean = false) :
    Type(), Map<K, V> by value {
    override fun toString(): String = value.toString()
}

abstract class BooleanType(override val value: Boolean, override val isTransient: Boolean = false) :
    Type(), Comparable<Boolean> {
    override fun toString(): String = value.toString()
    override fun compareTo(other: Boolean): Int = value.compareTo(other)
}

abstract class LongType(override val value: Long, override val isTransient: Boolean = false) :
    Type(), Comparable<Long> {
    override fun toString(): String = value.toString()
    override fun compareTo(other: Long): Int = value.compareTo(other)
}

abstract class DoubleType(override val value: Double, override val isTransient: Boolean = false) :
    Type(), Comparable<Double> {
    override fun toString(): String = value.toString()
    override fun compareTo(other: Double): Int = value.compareTo(other)
}

abstract class StringType(override val value: String, override val isTransient: Boolean = false) :
    Type(), Comparable<String>, CharSequence by value {
    override fun toString(): String = value
    override fun compareTo(other: String): Int = value.compareTo(other)
}

abstract class DateType(override val value: DateTime, override val isTransient: Boolean = false) :
    Type(), ReadableDateTime by value {
    override fun toString(): String = value.toString()
}

abstract class DateTimeType(override val value: DateTime, override val isTransient: Boolean = false) :
    Type(), ReadableDateTime by value {
    override fun toString(): String = value.toString()
}

abstract class TextType(override val value: String, override val isTransient: Boolean = false) :
    Type(), Comparable<String>, CharSequence by value {
    override fun toString(): String = value
    override fun compareTo(other: String): Int = value.compareTo(other)
}

abstract class BooleanListType(value: List<BooleanType>, isTransient: Boolean = false) :
    ListType<BooleanType>(value, isTransient)

abstract class LongListType(value: List<LongType>, isTransient: Boolean) :
    ListType<LongType>(value, isTransient)

abstract class DoubleListType(value: List<DoubleType>, isTransient: Boolean = false) :
    ListType<DoubleType>(value, isTransient)

abstract class StringListType(value: List<StringType>, isTransient: Boolean = false) :
    ListType<StringType>(value, isTransient)

abstract class TextListType(value: List<TextType>, isTransient: Boolean = false) :
    ListType<TextType>(value, isTransient)

abstract class DateListType(value: List<DateType>, isTransient: Boolean) :
    ListType<DateType>(value, isTransient)

abstract class DateTimeListType(value: List<DateTimeType>, isTransient: Boolean = false) :
    ListType<DateTimeType>(value, isTransient)

abstract class AttributeListType(value: Map<String, Cell>, isTransient: Boolean = true) :
    MapType<String, Cell>(value, isTransient)

