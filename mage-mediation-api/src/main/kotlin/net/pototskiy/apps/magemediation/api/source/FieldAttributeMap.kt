package net.pototskiy.apps.magemediation.api.source

import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute


data class FieldAttributeMap(private val fieldToAttr: Map<Field, AnyTypeAttribute>) :
    Map<Field, AnyTypeAttribute> by fieldToAttr {
    val fields: FieldCollection
        get() = FieldCollection(fieldToAttr.keys.toList())
    val attributes: AttributeCollection
        get() = AttributeCollection(fieldToAttr.values.toList())
}