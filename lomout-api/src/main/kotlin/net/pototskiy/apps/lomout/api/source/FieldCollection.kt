package net.pototskiy.apps.lomout.api.source

/**
 * Source field collection
 *
 * @property fields List<Field>
 * @constructor
 */
data class FieldCollection(private val fields: List<Field>) : List<Field> by fields
