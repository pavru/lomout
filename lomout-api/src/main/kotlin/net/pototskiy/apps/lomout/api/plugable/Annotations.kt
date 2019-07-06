package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.document.ExtraAttributeData
import kotlin.reflect.KClass

/**
 * Attribute reader.
 *
 * @property klass The attribute reader class
 * @property parameters The reader parameters
 * @constructor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@ExtraAttributeData
annotation class Reader(val klass: KClass<out ReaderBuilder>)

/**
 * Attribute writer.
 *
 * @property klass The attribute writer class
 * @property parameters The writer parameters
 * @constructor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@ExtraAttributeData
annotation class Writer(val klass: KClass<out WriterBuilder>)
