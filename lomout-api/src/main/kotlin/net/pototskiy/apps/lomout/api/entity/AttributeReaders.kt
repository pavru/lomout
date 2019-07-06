package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.AttributeReaders.readers
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plugable.Reader
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubtypeOf

/**
 * Attribute readers cache
 *
 * @property readers The cache
 */
private object AttributeReaders {
    val readers = mutableMapOf<DocumentMetadata.Attribute, AttributeReader<out Any?>>()
}

/**
 * Get or create attribute reader
 */
val DocumentMetadata.Attribute.reader: AttributeReader<out Any?>
    get() {
        return readers.getOrPut(this) {
            val readerFromAnnotation = this.annotations.find { it is Reader } as? Reader
            if (readerFromAnnotation != null) {
                val reader = try {
                    readerFromAnnotation.klass.createInstance().build()
                } catch (e: IllegalArgumentException) {
                    throw AppConfigException(badPlace(this), "Reader cannot be created.")
                }
                reader
            } else {
                defaultReaders.keys.find { this.type.isSubtypeOf(it) }
                    ?.let { defaultReaders[it] }
                    ?: throw AppConfigException(
                        badPlace(this),
                        "No default reader for an attribute of '${this.typeName}'."
                    )
            }
        }
    }
