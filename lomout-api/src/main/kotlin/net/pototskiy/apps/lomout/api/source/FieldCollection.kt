package net.pototskiy.apps.lomout.api.source

data class FieldCollection(private val fields: List<Field>) : List<Field> by fields
