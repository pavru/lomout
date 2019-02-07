package net.pototskiy.apps.magemediation.api.config.data


data class FieldCollection(private val fields: Map<Field, Attribute>): Map<Field, Attribute> by fields
