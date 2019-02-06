package net.pototskiy.apps.magemediation.api.config.data

import net.pototskiy.apps.magemediation.api.config.type.Attribute


data class FieldCollection(private val fields: Map<Field, Attribute>): Map<Field, Attribute> by fields
