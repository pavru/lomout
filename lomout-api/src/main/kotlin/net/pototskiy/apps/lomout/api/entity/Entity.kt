package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.entity.helper.loadEntityAttributes
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.plus
import org.jetbrains.exposed.dao.EntityID

/**
 * Data entity
 *
 * @property repository EntityRepositoryInterface
 * @property data MutableMap<Attribute<out Type>, Type>
 * @constructor
 */
class Entity internal constructor(type: EntityType, id: EntityID<Int>, repository: EntityRepositoryInterface) :
    EntityBase(type, id, repository) {
    /**
     * Entity attributes
     */
    val data: MutableMap<AnyTypeAttribute, Type> = mutableMapOf()

    internal fun loadAttributes() {
        data.clear()
        data.putAll(loadEntityAttributes(this))
    }

    /**
     * Set entity attribute
     *
     * @param attribute The attribute to set
     * @param value The attribute value
     */
    operator fun set(attribute: AnyTypeAttribute, value: Type?) {
        checkTypeHasAttribute(attribute)
        checkTypeIsSynthetic(attribute)
        checkAttributeIsNullable(attribute, value)
        checkAttributeType(attribute, value)
        if (value == null) {
            proceedAttributeDeletion(attribute)
        } else {
            if (data[attribute] == null) {
                proceedAttributeCreation(attribute, value)
            } else if (data[attribute] != value) {
                proceedAttributeUpdate(attribute, value)
            }
        }
        type.attributes
            .filter { it.isSynthetic }
            .forEach { attr ->
                val builder = attr.builder!!
                builder(this)?.let { data[attr] = it } ?: data.remove(attr)
            }
    }

    private fun checkAttributeType(attribute: AnyTypeAttribute, value: Type?) {
        if (value != null && !attribute.type.isInstance(value)) {
            throw AppDataException(badPlace(type) + attribute, "Value is not compatible with the attribute's type.")
        }
    }

    private fun checkTypeIsSynthetic(attribute: AnyTypeAttribute) {
        if (attribute.isSynthetic) {
            throw AppDataException(badPlace(attribute), "The transient attribute cannot be set")
        }
    }

    private fun checkAttributeIsNullable(attribute: AnyTypeAttribute, value: Type?) {
        if (!attribute.isNullable && value == null) {
            throw AppDataException(badPlace(attribute), "Trying to set null to non-nullable attribute")
        }
    }

    private fun proceedAttributeUpdate(
        attribute: AnyTypeAttribute,
        value: Type
    ) {
        if (value != data[attribute]) {
            data[attribute] = value
            repository.updateAttribute(this, attribute)
        }
    }

    private fun proceedAttributeCreation(
        attribute: AnyTypeAttribute,
        value: Type
    ) {
        data[attribute] = value
        repository.createAttribute(this, attribute)
    }

    private fun proceedAttributeDeletion(attribute: AnyTypeAttribute) {
        data.remove(attribute)
        repository.deleteAttribute(this, attribute)
    }

    /**
     * Set entity attribute
     *
     * @param attributeName The attribute name to set
     * @param value The attribute value
     */
    operator fun set(attributeName: String, value: Type?) =
        set(this.type.getAttribute(attributeName), value)

    /**
     * Get attribute value
     *
     * @param attribute The attribute to get
     * @return The attribute value
     */
    operator fun get(attribute: AnyTypeAttribute): Type? {
        checkTypeHasAttribute(attribute)
        if (!data.containsKey(attribute) && attribute.isSynthetic) {
            attribute.builder!!.invoke(this)?.let { data[attribute] = it }
        }
        return data[attribute]
    }

    /**
     * Get attribute value
     *
     * @param attributeName The attribute name
     * @return The attribute value
     */
    operator fun get(attributeName: String): Type? = get(type.getAttribute(attributeName))

    /**
     * Check entity type has the attribute
     *
     * @param attribute The entity attribute
     */
    private fun checkTypeHasAttribute(attribute: AnyTypeAttribute) {
        if (!type.attributes.contains(attribute)) {
            throw AppConfigException(badPlace(type) + attribute, "Entity type has no attribute")
        }
    }
}
