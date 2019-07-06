package net.pototskiy.apps.lomout.api.document

/**
 * Annotation for an additional attribute annotations
 *
 * Only annotations with this one will be added to attribute metadata
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class ExtraAttributeData
