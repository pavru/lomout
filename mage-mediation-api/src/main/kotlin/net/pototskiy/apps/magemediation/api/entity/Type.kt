package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.Generated
import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.entity.Type.Companion.TYPE_NOT_SUPPORT_SQL
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
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
            isTransient -> throw DatabaseException(TYPE_NOT_SUPPORT_SQL)
            this.sqlType != NoSqlColumn::class -> return this.sqlType
            else -> throw DatabaseException(TYPE_NOT_SUPPORT_SQL)
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
    throw DatabaseException(TYPE_NOT_SUPPORT_SQL)
}

sealed class ListType<T>(override val value: List<T>, override val isTransient: Boolean = false) :
    Type(), List<T> by value {
    override fun toString(): String = value.toString()
}

sealed class MapType<K, V>(override val value: Map<K, V>, override val isTransient: Boolean = false) :
    Type(), Map<K, V> by value {
    override fun toString(): String = value.toString()
}

abstract class BooleanType(
    override val value: Boolean,
    override val isTransient: Boolean = false,
    override val sqlType: KClass<BooleanColumnType> = BooleanColumnType::class
) : Type(), Comparable<BooleanType> {
    override fun toString(): String = value.toString()
    override fun compareTo(other: BooleanType): Int = value.compareTo(other.value)
}

abstract class LongType(
    override val value: Long,
    override val isTransient: Boolean = false,
    override val sqlType: KClass<LongColumnType> = LongColumnType::class
) :
    Type(), Comparable<LongType> {
    override fun toString(): String = value.toString()
    override fun compareTo(other: LongType): Int = value.compareTo(other.value)
}

abstract class DoubleType(
    override val value: Double,
    override val isTransient: Boolean = false,
    override val sqlType: KClass<DoubleColumnType> = DoubleColumnType::class
) :
    Type(), Comparable<DoubleType> {
    override fun toString(): String = value.toString()
    override fun compareTo(other: DoubleType): Int = value.compareTo(other.value)
}

abstract class StringType(
    override val value: String,
    override val isTransient: Boolean = false,
    override val sqlType: KClass<VarCharColumnType> = VarCharColumnType::class
) :
    Type(), Comparable<StringType>, CharSequence by value {
    override fun toString(): String = value
    override fun compareTo(other: StringType): Int = value.compareTo(other.value)
}

abstract class DateType(
    override val value: DateTime,
    override val isTransient: Boolean = false,
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
) :
    Type(), ReadableDateTime by value {
    override fun toString(): String = value.toString()
}

abstract class DateTimeType(
    override val value: DateTime,
    override val isTransient: Boolean = false,
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
) :
    Type(), ReadableDateTime by value {
    override fun toString(): String = value.toString()
}

abstract class TextType(
    override val value: String,
    override val isTransient: Boolean = false,
    override val sqlType: KClass<TextColumnType> = TextColumnType::class
) :
    Type(), Comparable<TextType>, CharSequence by value {
    override fun toString(): String = value
    override fun compareTo(other: TextType): Int = value.compareTo(other.value)
}

abstract class BooleanListType(
    value: List<BooleanType>,
    isTransient: Boolean = false,
    override val sqlType: KClass<BooleanColumnType> = BooleanColumnType::class
) :
    ListType<BooleanType>(value, isTransient)

abstract class LongListType(
    value: List<LongType>,
    isTransient: Boolean,
    override val sqlType: KClass<LongColumnType> = LongColumnType::class
) :
    ListType<LongType>(value, isTransient)

abstract class DoubleListType(
    value: List<DoubleType>,
    isTransient: Boolean = false,
    override val sqlType: KClass<DoubleColumnType> = DoubleColumnType::class
) :
    ListType<DoubleType>(value, isTransient)

abstract class StringListType(
    value: List<StringType>,
    isTransient: Boolean = false,
    override val sqlType: KClass<VarCharColumnType> = VarCharColumnType::class
) :
    ListType<StringType>(value, isTransient)

abstract class TextListType(
    value: List<TextType>,
    isTransient: Boolean = false,
    override val sqlType: KClass<TextColumnType> = TextColumnType::class
) :
    ListType<TextType>(value, isTransient)

abstract class DateListType(
    value: List<DateType>,
    isTransient: Boolean = false,
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
) :
    ListType<DateType>(value, isTransient)

abstract class DateTimeListType(
    value: List<DateTimeType>,
    isTransient: Boolean = false,
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
) :
    ListType<DateTimeType>(value, isTransient)

@Generated
class NoSqlColumn(override var nullable: Boolean = true) : IColumnType {
    override fun sqlType(): String = "no_sql_type"
}

abstract class AttributeListType(
    value: Map<String, Cell>,
    isTransient: Boolean = true,
    override val sqlType: KClass<NoSqlColumn> = NoSqlColumn::class
) :
    MapType<String, Cell>(value, isTransient)
