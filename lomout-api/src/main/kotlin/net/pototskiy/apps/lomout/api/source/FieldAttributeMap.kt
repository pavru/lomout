package net.pototskiy.apps.lomout.api.source

import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.AttributeCollection

/**
 * Source field to attribute map
 *
 * @property fieldToAttr Map<Field, Attribute<out Type>>
 * @property fields FieldCollection
 * @property attributes AttributeCollection
 * @constructor
 */
data class FieldAttributeMap(private val fieldToAttr: Map<Field, DocumentMetadata.Attribute>) :
    Map<Field, DocumentMetadata.Attribute> by fieldToAttr {
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
