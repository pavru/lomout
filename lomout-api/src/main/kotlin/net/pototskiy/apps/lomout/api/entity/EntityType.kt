package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.NamedObject
import net.pototskiy.apps.lomout.api.database.AttributeTable
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.unknownPlace

/**
 * Entity type configuration
 *
 * @property name String
 * @property open Boolean
 * @property manager EntityTypeManagerInternal
 * @property attributes List<Attribute<*>>
 * @constructor
 */
abstract class EntityType(
    override val name: String,
    val open: Boolean
) : NamedObject {

    /**
     * Entity type manager
     */
    lateinit var manager: EntityTypeManager

    /**
     * Entity attributes list
     */
    val attributes: List<Attribute<*>>
        get() = manager.getEntityTypeAttributes(this)

    internal val attributeTables: Array<AttributeTable<*>>
        get() = manager.getEntityAttributeTables(this)

    @Suppress("unused")
    internal val mainTable: DbEntityTable
        get() = manager.getEntityMainTable(this)

    /**
     * Get attribute by name
     *
     * @param name String The attribute name
     * @return Attribute<*>? The attribute or null if it's not found
     */
    fun getAttributeOrNull(name: String): Attribute<*>? {
        val attr = manager.getEntityAttribute(this, name)
            ?: return null
        return if (attr in attributes) attr else null
    }

    /**
     * Get attribute by name.
     *
     * @param name The attribute name
     * @return The attribute
     * @throws AppConfigException Attribute has not found
     */
    fun getAttribute(name: String): Attribute<*> = manager.getEntityAttribute(this, name)
        ?: throw AppConfigException(badPlace(this), "Attribute '$name' is not defined.")

    /**
     * Check if entity type has the attribute, with exception it's not
     *
     * @param attribute The attribute to check
     * @throws AppConfigException Attribute not defined for entity type
     */
    fun checkAttributeDefined(attribute: Attribute<*>) {
        if (!isAttributeDefined(attribute)) {
            throw AppConfigException(badPlace(attribute), "Entity '$this.name' has no attribute.")
        }
    }

    /**
     * Check if entity type has the attribute
     *
     * @param attribute Attribute<*>
     * @return Boolean true — defined, false — not defined
     */
    @PublicApi
    fun isAttributeDefined(attribute: Attribute<*>) = attributes.any { it.name == attribute.name }

    /**
     * Are attributes equal
     *
     * @param other Any?
     * @return Boolean
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityType) return false

        if (name != other.name) return false

        return true
    }

    /**
     * Attribute hash code
     *
     * @return Int
     */
    override fun hashCode(): Int {
        return name.hashCode()
    }

    /**
     * Get string presentation
     *
     * @return String
     */
    override fun toString(): String {
        return "EntityType(name='$name', open=$open)"
    }

    /**
     * Entity type builder class
     *
     * @property helper ConfigBuildHelper The builder helper
     * @property entityType String The entity type name
     * @property open Boolean Is open or not
     * @property attributes MutableList<Attribute<*>> The entity attributes list
     * @property inheritances MutableList<ParentEntityType> Super types of the entity type
     * @constructor
     */
    @ConfigDsl
    class Builder(
        val helper: ConfigBuildHelper,
        val entityType: String,
        private val open: Boolean
    ) {
        /**
         * Attribute list, **do not use in the configuration**
         */
        val attributes = mutableListOf<Attribute<*>>()
        private val inheritances = mutableListOf<ParentEntityType>()

        /**
         * Attribute definition
         *
         * ```
         * ...
         *  attribute<Type>("name") {
         *      key()
         *      nullable()
         *      builder<BuilderType> {
         *          // builder parameters, it depends on builder type
         *      }
         *      builder { entity ->
         *          // builder code
         *      }
         *      reader<ReaderType> {
         *          // reader parameters, it depends on reader type
         *      }
         *      reader { attribute, cell ->
         *          // reader code
         *      }
         *      writer<WriterType> {
         *          // writer parameters, it depends on writer type
         *      }
         *      writer { valueOfType, cell ->
         *          // writer code
         *      }
         *  }
         * ...
         * ```
         * * [Type][Type] — attribute value type, **mandatory**
         * * name — unique attribute name, scope is entity type, **mandatory**
         * * [key()][Attribute.Builder.key] — mark attribute as key, **at least one attribute must be marked as key**
         * * [nullable()][Attribute.Builder.nullable] — mark that attribute can have null value
         * * [builder][Attribute.Builder.builder] — builder plugin of function for the attribute
         *      that is not in the source, but can be built from another attributes,
         *      **only one builder can be defined**
         * * [reader][Attribute.Builder.reader] — plugin or function that is used to read attribute value from
         *  source cell, optional, if it's omitted default reader will be used
         * * [writer][Attribute.Builder.writer] — plugin or function to write attribute value to cell, optional,
         *  if it's omitted default type writer will be used
         *
         * @see Attribute
         * @see Attribute.Builder
         * @see Type
         *
         * @param name The attribute name
         * @param block The attribute configuration
         * @return true — attribute added
         */
        inline fun <reified T : Type> attribute(
            name: String,
            block: Attribute.Builder<T>.() -> Unit = {}
        ) =
            attributes.add(Attribute.Builder(helper, name, T::class).apply(block).build())

        /**
         * Inherit attributes from super entity type, *optional*
         *
         * ```
         * ...
         *  inheritFrom("name") {
         *      exclude("attr_name", "attr_name" ...)
         *      include("attr_name", "attr_name" ...)
         *  }
         * ...
         * ```
         * * [inheritFrom][ParentEntityType.Builder] — inherit attributes from super entity type, *optional*
         * * name: String — entity type name to inherit attributes
         * * [exclude][ParentEntityType.Builder.exclude] — list of attributes to exclude
         * * [include][ParentEntityType.Builder.include] — list of attribute to include
         *
         *
         * @param name The entity type name
         * @param block The inheritance configuration
         */
        fun inheritFrom(name: String, block: ParentEntityType.Builder.() -> Unit = {}) {
            val eType = helper.typeManager.getEntityType(name)
                ?: throw AppConfigException(unknownPlace(), "Entity type '$name' is not defined.")
            inheritances.add(ParentEntityType.Builder(helper, eType).apply(block).build())
        }

        /**
         * Build entity
         *E
         * @return EntityType
         */
        fun build(): EntityType {
            return helper.typeManager.createEntityType(entityType, inheritances, open).also {
                helper.typeManager.initialAttributeSetup(
                    it,
                    AttributeCollection(attributes)
                )
            }
        }
    }
}

/**
 * Get entity attribute by name
 *
 * @receiver EntityType The entity type to get attribute
 * @param attribute String The attribute name
 * @return Attribute<*>
 */
operator fun EntityType.get(attribute: String) = this.getAttribute(attribute)
