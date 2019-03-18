package net.pototskiy.apps.lomout.api.source

import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.AttributeCollection

data class FieldAttributeMap(private val fieldToAttr: Map<Field, AnyTypeAttribute>) :
    Map<Field, AnyTypeAttribute> by fieldToAttr {
    val fields: FieldCollection
        get() = FieldCollection(fieldToAttr.keys.toList())
    val attributes: AttributeCollection
        get() = AttributeCollection(fieldToAttr.values.toList())
}
