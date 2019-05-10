package net.pototskiy.apps.lomout.api

/**
 * Mark objects that should be ignored for "unused" inspection
 */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FIELD
)
annotation class PublicApi
