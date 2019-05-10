package net.pototskiy.apps.lomout.api

/**
 * Mark class or method to ignore it in coverage calculation
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Generated
