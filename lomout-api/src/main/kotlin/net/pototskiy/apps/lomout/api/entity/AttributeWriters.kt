package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
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
                    throw AppConfigException(
                        badPlace(this),
                        message("message.error.document.attribute.writer_cannot_create")
                    )
                }
                writer
            } else {
                defaultWriters.keys.find { this.type.isSubtypeOf(it) }
                    ?.let { defaultWriters[it] }
                    ?: throw AppConfigException(
                        badPlace(this),
                        message("message.error.document.attribute.no_default_writer", this.typeName)
                    )
            }
        }
    }
