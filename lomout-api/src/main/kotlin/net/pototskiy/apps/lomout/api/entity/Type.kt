package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.badData
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

/**
 * Attribute value type
 *
 * @property value Any The value
 * @property isTransient Boolean Is value transient (not saved to DB)
 * @property sqlType KClass<out IColumnType> The value SQL type
 */
sealed class Type {
    abstract val value: Any
    abstract val isTransient: Boolean
    protected abstract val sqlType: KClass<out IColumnType>

    /**
     * Is values are equals
     *
     * @param other Any?
     * @return Boolean
     */
    @Generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Type) return false
        if (other.value != this.value) return false

        return true
    }

    /**
     * Value hash code
     *
     * @return Int
     */
    @Generated
    override fun hashCode(): Int {
        return this.value.hashCode()
    }

    /**
     * Get column type relate to value type
     *
     * @return KClass<out IColumnType>
     */
    fun sqlType(): KClass<out IColumnType> {
        when {
            isTransient -> throw AppDataException(badData(this), TYPE_NOT_SUPPORT_SQL)
            this.sqlType != NoSqlColumn::class -> return this.sqlType
            else -> throw AppDataException(badData(value), TYPE_NOT_SUPPORT_SQL)
        }
    }

    /**
     * Is value a list
     *
     * @return Boolean
     */
    fun isList() = this is ListType<*>

    /**
     * Is value a map
     *
     * @return Boolean
     */
    fun isMap() = this is MapType<*, *>

    /**
     * Is value single, not list and map
     *
     * @return Boolean
     */
    fun isSingle() = !(this is ListType<*> || this is MapType<*, *>)

    /**
     * Is value instance of given type
     *
     * @param T Type The given type
     * @return Boolean
     */
    @Generated
    inline fun <reified T : Type> isTypeOf() = this is T

    /**
     * Companion object
     */
    companion object {
        /**
         * Error message for unsupported types
         */
        const val TYPE_NOT_SUPPORT_SQL = "Type does not support sql column type."
    }
}

/**
 * Is test type a list
 *
 * @receiver KClass<out Type> The test type
 * @return Boolean
 */
@PublicApi
fun KClass<out Type>.isList(): Boolean = this.isSubclassOf(ListType::class)

/**
 * Is test type a map
 *
 * @receiver KClass<out Type> The test type
 * @return Boolean
 */
@PublicApi
fun KClass<out Type>.isMap(): Boolean = this.isSubclassOf(MapType::class)

/**
 * Is test type single, not list or map type
 *
 * @receiver KClass<out Type> The test type
 * @return Boolean
 */
@PublicApi
fun KClass<out Type>.isSingle(): Boolean = !(this.isSubclassOf(ListType::class) || this.isSubclassOf(MapType::class))

/**
 * Is test type instance of given one
 *
 * @receiver KClass<out Type> The test type
 * @param T Type The given type
 * @return Boolean
 */
@Generated
inline fun <reified T : Type> KClass<out Type>.isTypeOf(): Boolean {
    return this.isSubclassOf(T::class)
}

/**
 * Get related SQL column type of type
 *
 * @receiver KClass<out Type>
 * @return KClass<out IColumnType>
 */
fun KClass<out Type>.sqlType(): KClass<out IColumnType> {
    this.memberProperties.find { it.name == "sqlType" }?.let {
        val type = it.returnType.arguments[0].type?.classifier as KClass<*>
        if (type.isSubclassOf(IColumnType::class) && !type.isSubclassOf(NoSqlColumn::class)) {
            @Suppress("UNCHECKED_CAST")
            return type as KClass<out IColumnType>
        }
    }
    throw AppDataException(badData(this), TYPE_NOT_SUPPORT_SQL)
}

/**
 * Base list type
 *
 * @param T
 * @property value List<T>
 * @property isTransient Boolean
 * @constructor
 */
sealed class ListType<T>(override val value: List<T>, override val isTransient: Boolean = false) :
    Type(), List<T> by value {
    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()
}

/**
 * Base map type
 *
 * @param K
 * @param V
 * @property value Map<K, V>
 * @property isTransient Boolean
 * @constructor
 */
sealed class MapType<K, V>(override val value: Map<K, V>, override val isTransient: Boolean = false) :
    Type(), Map<K, V> by value {
    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()
}

/**
 * Boolean type
 *
 * @property value Boolean The value
 * @property isTransient Boolean Transient flag
 * @property sqlType KClass<BooleanColumnType> SQL column type
 * @constructor
 */
class BooleanType(
    override val value: Boolean,
    override val isTransient: Boolean = false
) : Type(), Comparable<BooleanType> {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<BooleanColumnType> = BooleanColumnType::class

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()

    /**
     * Compare values
     *
     * @param other BooleanType
     * @return Int
     */
    override fun compareTo(other: BooleanType): Int = value.compareTo(other.value)
}

/**
 * Long type
 *
 * @property value Long The value
 * @property isTransient Boolean Transient flag
 * @property sqlType KClass<LongColumnType> The SQL column type
 * @constructor
 */
open class LongType(
    override val value: Long,
    override val isTransient: Boolean = false
) : Type(), Comparable<LongType> {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<LongColumnType> = LongColumnType::class

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()

    /**
     * Compare values
     *
     * @param other LongType
     * @return Int
     */
    override fun compareTo(other: LongType): Int = value.compareTo(other.value)
}

/**
 * Double type
 *
 * @property value Double The value
 * @property isTransient Boolean Transient flag
 * @property sqlType KClass<DoubleColumnType> The SQL column type
 * @constructor
 */
class DoubleType(
    override val value: Double,
    override val isTransient: Boolean = false
) : Type(), Comparable<DoubleType> {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<DoubleColumnType> = DoubleColumnType::class

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()

    /**
     * Compare values
     *
     * @param other DoubleType
     * @return Int
     */
    override fun compareTo(other: DoubleType): Int = value.compareTo(other.value)
}

/**
 * String type
 *
 * @property value String The value
 * @property isTransient Boolean Transient flag
 * @property sqlType KClass<VarCharColumnType> The SQL column type
 * @constructor
 */
class StringType(
    override val value: String,
    override val isTransient: Boolean = false
) : Type(), Comparable<StringType>, CharSequence by value {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<VarCharColumnType> = VarCharColumnType::class

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value

    /**
     * Compare values
     *
     * @param other StringType
     * @return Int
     */
    override fun compareTo(other: StringType): Int = value.compareTo(other.value)
}

/**
 * Date type
 *
 * @property value DateTime The value
 * @property isTransient Boolean Transient flag
 * @property sqlType KClass<DateColumnType> The SQL column type
 * @constructor
 */
class DateType(
    override val value: DateTime,
    override val isTransient: Boolean = false
) : Type(), ReadableDateTime by value {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<DateColumnType> = DateColumnType::class

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()
}

/**
 * DateTime type
 *
 * @property value DateTime The value
 * @property isTransient Boolean Transient flag
 * @property sqlType KClass<DateColumnType> The SQL column type
 * @constructor
 */
class DateTimeType(
    override val value: DateTime,
    override val isTransient: Boolean = false
) : Type(), ReadableDateTime by value {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<DateColumnType> = DateColumnType::class

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()
}

/**
 * Text type
 *
 * @property value String The value
 * @property isTransient Boolean Transient flag
 * @property sqlType KClass<TextColumnType> The SQL column type
 * @constructor
 */
class TextType(
    override val value: String,
    override val isTransient: Boolean = false
) : Type(), Comparable<TextType>, CharSequence by value {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<TextColumnType> = TextColumnType::class

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value

    /**
     * Compare values
     *
     * @param other TextType
     * @return Int
     */
    override fun compareTo(other: TextType): Int = value.compareTo(other.value)
}

/**
 * List of boolean type
 *
 * @property sqlType KClass<BooleanColumnType> The SQL column type
 * @constructor
 */
class BooleanListType(
    value: List<BooleanType>,
    isTransient: Boolean = false
) : ListType<BooleanType>(value, isTransient) {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<BooleanColumnType> = BooleanColumnType::class
}

/**
 * List of long type
 *
 * @property sqlType KClass<LongColumnType> The SQL column type
 * @constructor
 */
class LongListType(
    value: List<LongType>,
    isTransient: Boolean = false
) : ListType<LongType>(value, isTransient) {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<LongColumnType> = LongColumnType::class
}

/**
 * List of double type
 *
 * @property sqlType KClass<DoubleColumnType> The SQL column type
 * @constructor
 */
class DoubleListType(
    value: List<DoubleType>,
    isTransient: Boolean = false
) : ListType<DoubleType>(value, isTransient) {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<DoubleColumnType> = DoubleColumnType::class
}

/**
 * List of string type
 *
 * @property sqlType KClass<VarCharColumnType> The SQL column type
 * @constructor
 */
class StringListType(
    value: List<StringType>,
    isTransient: Boolean = false
) : ListType<StringType>(value, isTransient) {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<VarCharColumnType> = VarCharColumnType::class
}

/**
 * List of text type
 *
 * @property sqlType KClass<TextColumnType> The SQL column type
 * @constructor
 */
class TextListType(
    value: List<TextType>,
    isTransient: Boolean = false
) : ListType<TextType>(value, isTransient) {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<TextColumnType> = TextColumnType::class
}

/**
 * List of date type
 *
 * @property sqlType KClass<DateColumnType> The SQL column type
 * @constructor
 */
class DateListType(
    value: List<DateType>,
    isTransient: Boolean = false
) : ListType<DateType>(value, isTransient) {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
}

/**
 * List of DateTime type
 *
 * @property sqlType KClass<DateColumnType> The SQL column type
 * @constructor
 */
class DateTimeListType(
    value: List<DateTimeType>,
    isTransient: Boolean = false
) : ListType<DateTimeType>(value, isTransient) {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<DateColumnType> = DateColumnType::class
}

/**
 * Indicator of non using SQL
 *
 * @property nullable Boolean Nullable flag
 * @constructor
 */
@Generated
class NoSqlColumn(override var nullable: Boolean = true) : IColumnType {
    /**
     * Related SQL type
     */
    override fun sqlType(): String = "no_sql_type"
}

/**
 * List of attributes type
 *
 * @property sqlType KClass<NoSqlColumn> The SQL column type
 * @constructor
 */
class AttributeListType(
    value: Map<String, Cell>,
    isTransient: Boolean = true
) : MapType<String, Cell>(value, isTransient) {
    /**
     * Related SQL type
     */
    override val sqlType: KClass<NoSqlColumn> = NoSqlColumn::class
}

/**
 * Convert single type to list type
 *
 * @receiver Type
 * @return ListType<*>
 * @throws AppDataException The value is already list type
 */
fun Type.toList(): ListType<*> {
    return when (this) {
        is BooleanListType,
        is LongListType,
        is DoubleListType,
        is StringListType,
        is TextListType,
        is DateListType,
        is DateTimeListType,
        is AttributeListType -> throw AppDataException(badData(this), "Value already is list.")
        is BooleanType -> BooleanListType(listOf(this))
        is LongType -> LongListType(listOf(this))
        is DoubleType -> DoubleListType(listOf(this))
        is StringType -> StringListType(listOf(this))
        is DateType -> DateListType(listOf(this))
        is DateTimeType -> DateTimeListType(listOf(this))
        is TextType -> TextListType(listOf(this))
    }
}
