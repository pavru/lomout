package net.pototskiy.apps.lomout.api.document

/**
 * Attribute default field name.
 *
 * @property name String
 * @constructor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class FieldName(val name: String)
