package net.pototskiy.apps.lomout.api.entity

data class AttributeCollection(private val attributes: List<Attribute<out Type>>) :
    List<Attribute<out Type>> by attributes
