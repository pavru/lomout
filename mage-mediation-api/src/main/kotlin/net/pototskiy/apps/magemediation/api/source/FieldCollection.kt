package net.pototskiy.apps.magemediation.api.source

data class FieldCollection(private val fields: List<Field>): List<Field> by fields
