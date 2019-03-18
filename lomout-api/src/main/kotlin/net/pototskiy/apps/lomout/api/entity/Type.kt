package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.entity.Type.Companion.TYPE_NOT_SUPPORT_SQL
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType
import org.joda.time.DateTime
import org.joda.time.ReadableDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

sealed class Type {
    abstract val value: Any
    abstract val isTransient: Boolean
    protected abstract val sqlType: KClass<out IColumnType>

    @Generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Type) return false
        if (other.value != this.value) return false

        return true
    }

    @Generated
    override fun hashCode(): Int {
        return this.value.hashCode()
    }

    fun sqlType(): KClass<out IColumnType> {
        when {
            isTransient -> throw AppDataException(TYPE_NOT_SUPPORT_SQL)
            this.sqlType != NoSqlColumn::class -> return this.sqlType
            else -> throw AppDataException(TYPE_NOT_SUPPORT_SQL)
        }
    }

    fun isList() = this is ListType<*>
    fun isMap() = this is MapType<*, *>
    fun isSingle() = !(this is ListType<*> || this is MapType<*, *>)
    @Generated
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

@Generated
inline fun <reified T : Type> KClass<out Type>.isTypeOf(): Boolean {
    return this.isSubclassOf(T::class)
}

fun KClass<out Type>.sqlType(): KClass<out IColumnType> {
    this.memberProperties.find { it.name == "sqlType" }?.let {
        val type = it.returnType.arguments[0].type?.classifier as KClass<*>
        if (type.isSubclassOf(IColumnType::class) && !type.isSubclassOf(NoSqlColumn::class)) {
            @Suppress("UNCHECKED_CAST")
            return type as KClass<out IColumnType>
        }
    }
    throw AppDataException(TYPE_NOT_SUPPORT_SQL)
}

sealed class ListType<T>(override val value: List<T>, override val isTransient: Boolean = false) :
    Type(), List<T> by value {
    override fun toString(): String = value.toString()
}

sealed class MapType<K, V>(override val value: Map<K, V>, override val isTransient: Boolean = false) :
    Type(), Map<K, V> by value {
    override fun toString(): String = value.toString()
}

class BooleanType(
    override val value: Boolean,
    override val isTransient: Boolean = false
) : Type(), Comparable<BooleanType> {
    override val sqlType: KClass<BooleanColumnType> = BooleanColumnType::class
    override fun toString(): String = value.toString()
    override fun compareTo(other: BooleanType): Int = value.compareTo(other.value)
}

open class LongType(
    override val value: Long,
    override val isTransient: Boolean = false
) : Type(), Comparable<LongType> {
    override val sqlType: KClass<LongColumnType> = LongColumnType::class
    override fun toString(): String = value.toString()
    override fun compareTo(other: LongType): Int = value.compareTo(other.value)
}

class DoubleType(
    override val value: Double,
    override val isTransient: Boolean = false
) : Type(), Comparable<DoubleType> {
    override val sqlType: KClass<DoubleColumnType> = DoubleColumnType::class
    override fun toString(): String = value.toString()
    override fun compareTo(other: DoubleType): Int = value.compareTo(other.value)
}

class StringType(
    override val value: String,
    override val isTransient: Boolean = false
) : Type(), Comparable<StringType>, CharSequence by value {
    override val sqlType: KClass<VarCharColumnType> = VarCharColumnType::class
    override fun toString(): String = value
    override fun compareTo(other: StringType): Int = value.compareTo(other.value)
}

class DateType(
    override val value: DateTime,
    override val isTransient: Boolean = false
) : Type(), ReadableDateTime by value {
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
    override fun toString(): String = value.toString()
}

class DateTimeType(
    override val value: DateTime,
    override val isTransient: Boolean = false
) : Type(), ReadableDateTime by value {
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
    override fun toString(): String = value.toString()
}

class TextType(
    override val value: String,
    override val isTransient: Boolean = false
) : Type(), Comparable<TextType>, CharSequence by value {
    override val sqlType: KClass<TextColumnType> = TextColumnType::class
    override fun toString(): String = value
    override fun compareTo(other: TextType): Int = value.compareTo(other.value)
}

class BooleanListType(
    value: List<BooleanType>,
    isTransient: Boolean = false
) : ListType<BooleanType>(value, isTransient) {
    override val sqlType: KClass<BooleanColumnType> = BooleanColumnType::class
}

class LongListType(
    value: List<LongType>,
    isTransient: Boolean = false
) : ListType<LongType>(value, isTransient) {
    override val sqlType: KClass<LongColumnType> = LongColumnType::class
}

class DoubleListType(
    value: List<DoubleType>,
    isTransient: Boolean = false
) : ListType<DoubleType>(value, isTransient) {
    override val sqlType: KClass<DoubleColumnType> = DoubleColumnType::class
}

class StringListType(
    value: List<StringType>,
    isTransient: Boolean = false
) : ListType<StringType>(value, isTransient) {
    override val sqlType: KClass<VarCharColumnType> = VarCharColumnType::class
}

class TextListType(
    value: List<TextType>,
    isTransient: Boolean = false
) : ListType<TextType>(value, isTransient) {
    override val sqlType: KClass<TextColumnType> = TextColumnType::class
}

class DateListType(
    value: List<DateType>,
    isTransient: Boolean = false
) : ListType<DateType>(value, isTransient) {
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
}

class DateTimeListType(
    value: List<DateTimeType>,
    isTransient: Boolean = false
) : ListType<DateTimeType>(value, isTransient) {
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
}

@Generated
class NoSqlColumn(override var nullable: Boolean = true) : IColumnType {
    override fun sqlType(): String = "no_sql_type"
}

class AttributeListType(
    value: Map<String, Cell>,
    isTransient: Boolean = true
) : MapType<String, Cell>(value, isTransient) {
    override val sqlType: KClass<NoSqlColumn> = NoSqlColumn::class
}

fun Type.toList(): ListType<*> {
    return when (this) {
        is BooleanListType,
        is LongListType,
        is DoubleListType,
        is StringListType,
        is TextListType,
        is DateListType,
        is DateTimeListType,
        is AttributeListType -> throw AppDataException("Value already is list")
        is BooleanType -> BooleanListType(listOf(this))
        is LongType -> LongListType(listOf(this))
        is DoubleType -> DoubleListType(listOf(this))
        is StringType -> StringListType(listOf(this))
        is DateType -> DateListType(listOf(this))
        is DateTimeType -> DateTimeListType(listOf(this))
        is TextType -> TextListType(listOf(this))
    }
}
