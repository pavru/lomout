package net.pototskiy.apps.lomout.api.source

import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.AttributeCollection

/**
 * Source field to attribute map
 *
 * @property fieldToAttr Map<Field, Attribute<out Type>>
 * @property fields FieldCollection
 * @property attributes AttributeCollection
 * @constructor
 */
data class FieldAttributeMap(private val fieldToAttr: Map<Field, AnyTypeAttribute>) :
    Map<Field, AnyTypeAttribute> by fieldToAttr {
    /**
     * Fields
     */
    val fields: FieldCollection
        get() = FieldCollection(fieldToAttr.keys.toList())
    /**
     * Attributes
     */
    val attributes: AttributeCollection
        get() = AttributeCollection(fieldToAttr.values.toList())
}
