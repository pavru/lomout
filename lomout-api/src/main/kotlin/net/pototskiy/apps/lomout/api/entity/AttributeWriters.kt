package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.plugable.Writer
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubtypeOf

/**
 * Attribute writers cache
 */
object AttributeWriters {
    /**
     * Found attribute writers
     */
    val writers = mutableMapOf<DocumentMetadata.Attribute, AttributeWriter<out Any?>>()
}

/**
 * Attribute writer
 */
val DocumentMetadata.Attribute.writer: AttributeWriter<out Any?>
    get() {
        return AttributeWriters.writers.getOrPut(this) {
            val writerFromAnnotation = this.annotations.find { it is Writer } as? Writer
            if (writerFromAnnotation != null) {
                val writer = try {
                    writerFromAnnotation.klass.createInstance().build()
                } catch (e: IllegalArgumentException) {
                    throw AppConfigException(badPlace(this), "Cannot create attribute writer.")
                }
                writer
            } else {
                defaultWriters.keys.find { this.type.isSubtypeOf(it) }
                    ?.let { defaultWriters[it] }
                    ?: throw AppConfigException(badPlace(this), "Unsupported attribute type '${this.typeName}'.")
            }
        }
    }
