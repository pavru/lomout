package net.pototskiy.apps.lomout.api.entity.type

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.badData
import net.pototskiy.apps.lomout.api.database.AttributeTable
import net.pototskiy.apps.lomout.api.database.EntityBooleans
import net.pototskiy.apps.lomout.api.database.EntityDateTimes
import net.pototskiy.apps.lomout.api.database.EntityDates
import net.pototskiy.apps.lomout.api.database.EntityDoubles
import net.pototskiy.apps.lomout.api.database.EntityLongs
import net.pototskiy.apps.lomout.api.database.EntityStrings
import net.pototskiy.apps.lomout.api.database.EntityTexts
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.unknownPlace
import org.joda.time.DateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.isSubclassOf

/**
 * Attribute value type
 *
 * @property value Any The value
 */
sealed class Type {
    abstract val value: Any
    internal abstract val table: AttributeTable<*>

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
}

private open class PersistentTypeCompanion(
    val table: AttributeTable<*>
)

/**
 * Base persistent list type
 *
 * @param T
 * @property value The value
 * @constructor
 */
sealed class ListType<T : Type>(override val value: List<T>) : Type(), List<T> by value {
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
 * @property value The value
 * @constructor
 */
sealed class MapType<K, V>(override val value: Map<K, V>) : Type(), Map<K, V> by value {
    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()
}

/**
 * Is test type a list
 *
 * @receiver KClass<out Type> The test type
 * @return Boolean
 */
@Suppress("unused")
@PublicApi
fun KClass<out Type>.isList(): Boolean = this.isSubclassOf(ListType::class)

/**
 * Is test type a map
 *
 * @receiver KClass<out Type> The test type
 * @return Boolean
 */
@Suppress("unused")
@PublicApi
fun KClass<out Type>.isMap(): Boolean = this.isSubclassOf(MapType::class)

/**
 * Is test type single, not list or map type
 *
 * @receiver KClass<out Type> The test type
 * @return Boolean
 */
@Suppress("unused")
@PublicApi
fun KClass<out Type>.isSingle(): Boolean = !(this.isList() || this.isMap())

/**
 * Is test type instance of given one
 *
 * @receiver KClass<out Type> The test type
 * @param T Type The given type
 * @return Boolean
 */
@Suppress("unused")
@Generated
inline fun <reified T : Type> KClass<out Type>.isTypeOf(): Boolean {
    return this.isSubclassOf(T::class)
}

internal val KClass<out Type>.table: AttributeTable<*>
    get() = (this.companionObjectInstance as? PersistentTypeCompanion)?.table
        ?: throw AppDataException(unknownPlace(), "Type has no store table.")

/**
 * Boolean type
 *
 * @property value Boolean The value
 * @constructor
 */
data class BOOLEAN(override val value: Boolean) : Type() {
    override val table: AttributeTable<*> = Companion.table

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityBooleans)
}

/**
 * Long type
 *
 * @property value Long The value
 * @constructor
 */
data class LONG(override val value: Long) : Type() {
    override val table: AttributeTable<*> = Companion.table

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityLongs)
}

/**
 * Double type
 *
 * @property value Double The value
 * @constructor
 */
data class DOUBLE(override val value: Double) : Type() {
    override val table: AttributeTable<*> = Companion.table

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityDoubles)
}

/**
 * String type
 *
 * @property value String The value
 * @constructor
 */
data class STRING(override val value: String) : Type(), CharSequence by value {
    override val table: AttributeTable<*> = Companion.table

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value

    private companion object : PersistentTypeCompanion(EntityStrings)
}

/**
 * Date type
 *
 * @property value DateTime The value
 * @constructor
 */
data class DATE(override val value: DateTime) : Type() {
    override val table: AttributeTable<*> = Companion.table

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityDates)
}

/**
 * DateTime type
 *
 * @property value DateTime The value
 * @constructor
 */
data class DATETIME(override val value: DateTime) : Type() {
    override val table: AttributeTable<*> = Companion.table

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityDateTimes)
}

/**
 * Text type
 *
 * @property value String The value
 * @constructor
 */
data class TEXT(override val value: String) : Type(), CharSequence by value {
    override val table: AttributeTable<*> = Companion.table

    /**
     * To string
     *
     * @return String
     */
    override fun toString(): String = value

    private companion object : PersistentTypeCompanion(EntityTexts)
}

/**
 * List of boolean type
 *
 * @constructor
 */
data class BOOLEANLIST(override val value: List<BOOLEAN>) :
    ListType<BOOLEAN>(value) {
    @Suppress("unused")
    constructor(other: BOOLEANLIST) : this(other.value)

    override val table: AttributeTable<*> = Companion.table

    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityBooleans)
}

/**
 * List of long type
 *
 * @constructor
 */
data class LONGLIST(override val value: List<LONG>) :
    ListType<LONG>(value) {
    @Suppress("unused")
    constructor(other: LONGLIST) : this(other.value)

    override val table: AttributeTable<*> = Companion.table

    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityLongs)
}

/**
 * List of double type
 *
 * @constructor
 */
data class DOUBLELIST(override val value: List<DOUBLE>) :
    ListType<DOUBLE>(value) {
    @Suppress("unused")
    constructor(other: DOUBLELIST) : this(other.value)

    override val table: AttributeTable<*> = Companion.table

    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityDoubles)
}

/**
 * List of string type
 *
 * @constructor
 */
data class STRINGLIST(override val value: List<STRING>) :
    ListType<STRING>(value) {
    @Suppress("unused")
    constructor(other: STRINGLIST) : this(other.value)

    override val table: AttributeTable<*> = Companion.table

    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityStrings)
}

/**
 * List of text type
 *
 * @constructor
 */
data class TEXTLIST(override val value: List<TEXT>) :
    ListType<TEXT>(value) {
    @Suppress("unused")
    constructor(other: TEXTLIST) : this(other.value)

    override val table: AttributeTable<*> = Companion.table

    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityTexts)
}

/**
 * List of date type
 *
 * @constructor
 */
data class DATELIST(override val value: List<DATE>) :
    ListType<DATE>(value) {
    @Suppress("unused")
    constructor(other: DATELIST) : this(other.value)

    override val table: AttributeTable<*> = Companion.table

    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityDates)
}

/**
 * List of DateTime type
 *
 * @constructor
 */
data class DATETIMELIST(override val value: List<DATETIME>) :
    ListType<DATETIME>(value) {
    @Suppress("unused")
    constructor(other: DATETIMELIST) : this(other.value)

    override val table: AttributeTable<*> = Companion.table

    override fun toString(): String = value.toString()

    private companion object : PersistentTypeCompanion(EntityDateTimes)
}

/**
 * List of attributes type
 *
 * @constructor
 */
data class ATTRIBUTELIST(override val value: Map<String, Cell>) : MapType<String, Cell>(value) {
    override val table: AttributeTable<*>
        get() = throw AppDataException(badData(this), "Type has no store table.")

    @Suppress("unused")
    constructor(other: ATTRIBUTELIST) : this(other.value)

    override fun toString(): String = value.toString()
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
        is BOOLEANLIST,
        is LONGLIST,
        is DOUBLELIST,
        is STRINGLIST,
        is TEXTLIST,
        is DATELIST,
        is DATETIMELIST,
        is ATTRIBUTELIST -> throw AppDataException(badData(this), "Value already is list.")
        is BOOLEAN -> BOOLEANLIST(listOf(this))
        is LONG -> LONGLIST(listOf(this))
        is DOUBLE -> DOUBLELIST(listOf(this))
        is STRING -> STRINGLIST(listOf(this))
        is DATE -> DATELIST(listOf(this))
        is DATETIME -> DATETIMELIST(listOf(this))
        is TEXT -> TEXTLIST(listOf(this))
    }
}
