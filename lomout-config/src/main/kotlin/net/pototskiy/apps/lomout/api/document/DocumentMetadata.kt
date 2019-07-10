/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.pototskiy.apps.lomout.api.document

import net.pototskiy.apps.lomout.api.MessageBundle.message
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties

/**
 * Document metadata
 *
 * @property klass The document class
 * @property collectionName The mongodb collection name
 * @property attributes The attribute maps
 * @property keyAttributes The list of key attributes
 * @property indexes The list of indexes
 * @constructor
 */
abstract class DocumentMetadata(val klass: KClass<out Document>) {
    val collectionName: String
    val attributes: Map<String, Attribute>
    val keyAttributes: List<Attribute>

    init {
        val v = klass.memberProperties.mapNotNull { property ->
            if (!property.hasAnnotation<NonAttribute>() &&
                property is KMutableProperty1<*, *> &&
                property.name !in basePropertyNames
            ) {
                if (!Document.supportedTypes.types.any { property.returnType.isSubtypeOf(it) }) {
                    throw DocumentException(
                        message(
                            "message.error.document.attribute.not_supported_type",
                            property.returnType,
                            property.name,
                            this::class.qualifiedName
                        )
                    )
                }
                val indexAnnotations = when {
                    property.hasAnnotation<Index>() -> arrayOf(property.findAnnotation()!!)
                    property.hasAnnotation<Indexes>() -> property.findAnnotation<Indexes>()!!.indexes
                    else -> emptyArray()
                }
                if (property.hasAnnotation<Key>() &&
                    (property.returnType.isMarkedNullable ||
                            Document.supportedTypes.listTypes.any { property.returnType.isSubtypeOf(it) })
                ) {
                    throw DocumentException(
                        message("message.error.document.attribute.key_is_nullable", property.name, klass.qualifiedName)
                    )
                }
                property.name to Attribute(
                    property.name,
                    property.returnType.classifier as KClass<*>,
                    property.returnType,
                    klass,
                    property as KMutableProperty1<out Document, *>,
                    property.returnType.toString(),
                    if (!Document.supportedTypes.listTypes.any { property.returnType.isSubtypeOf(it) }) {
                        property.returnType.classifier as KClass<*>
                    } else {
                        property.returnType.arguments.first().type!!.classifier as KClass<*>
                    },
                    property.hasAnnotation<Key>(),
                    property.returnType.isMarkedNullable,
                    indexAnnotations,
                    property.annotations.filter {
                        it.annotationClass.hasAnnotation<ExtraAttributeData>()
                    }.toTypedArray(),
                    property.getter,
                    property.setter,
                    property.findAnnotation<FieldName>()?.name ?: property.name
                )
            } else {
                null
            }
        }.toMap()
        collectionName = klass.simpleName!!.toLowerCase()
        attributes = v
        keyAttributes = v.values.filter { it.isKey }
    }

    /**
     * Document attribute metadata
     *
     * @property name The attribute name
     * @property klass The attribute class
     * @property type The attribute type [KType]
     * @property owner The attribute owner class
     * @property property The attribute property
     * @property typeName The attribute type name
     * @property listParameter The class of list members
     * @property isKey Boolean
     * @property isNullable Boolean
     * @property indexAnnotations The list of index annotation
     * @property annotations The list attribute annotations
     * @property getter The attribute property getter
     * @property setter The attribute property setter
     * @property fieldName The default field name for the attribute
     * @property annotationIndex Index of index annotations
     * @constructor
     */
    class Attribute(
        val name: String,
        val klass: KClass<*>,
        val type: KType,
        val owner: KClass<out Document>,
        val property: KMutableProperty1<out Document, *>,
        val typeName: String,
        val listParameter: KClass<*>,
        val isKey: Boolean = false,
        val isNullable: Boolean = true,
        val indexAnnotations: Array<Index>,
        val annotations: Array<Annotation> = emptyArray(),
        val getter: KProperty1.Getter<*, *>,
        val setter: KMutableProperty1.Setter<*, *>,
        val fieldName: String
    ) {
        val annotationIndex = annotations.map { it.annotationClass to it }.toMap()
        /**
         * Equals function
         * @param other Other index
         * @return Boolean
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Attribute

            if (name != other.name) return false
            if (owner != other.owner) return false

            return true
        }

        /**
         * Hash code function
         * @return Int
         */
        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + owner.hashCode()
            return result
        }

        /**
         * Returns a string representation of the object.
         */
        override fun toString(): String {
            return "Attribute (${owner.simpleName}:$name)"
        }
    }

    val indexes = listIndexes()

    private fun listIndexes(): List<JsonIndex> {
        val keyIndexes = attributes.values.filter { it.isKey }
            .map { JsonIndexKey(it.name, Index.SortOrder.ASC) }
            .run {
                if (this.isEmpty()) {
                    emptyList()
                } else {
                    listOf(JsonIndex("key_index", this, true))
                }
            }
        val addIndexes =
            attributes.values.asSequence().filter { it.indexAnnotations.isNotEmpty() }
                .map { attr -> attr.indexAnnotations.map { attr.name to it } }
                .flatten()
                .groupBy { it.second.name }
                .map { index ->
                    val keys = index.value.map { JsonIndexKey(it.first, it.second.sortOrder) }
                    JsonIndex(index.key, keys, index.value.first().second.isUnique)
                }.toList()
        return keyIndexes.plus(addIndexes)
    }

    /**
     * Index key
     *
     * @property name The attribute name
     * @property sortOrder The sort order
     * @constructor
     */
    data class JsonIndexKey(
        val name: String,
        val sortOrder: Index.SortOrder
    )

    /**
     * Index
     *
     * @property name The index name
     * @property keys The index keys
     * @property isUnique
     * @constructor
     */
    data class JsonIndex(
        val name: String,
        val keys: List<JsonIndexKey>,
        val isUnique: Boolean
    )

    companion object {
        private val basePropertyNames = Document::class.memberProperties.map { it.name }
    }
}

/**
 * Get document metadata
 */
val KClass<out Document>.documentMetadata: DocumentMetadata
    get() {
        return this.companionObjectInstance as? DocumentMetadata
            ?: throw DocumentException(
                message(
                    "message.error.document.no_companion",
                    this::class.qualifiedName,
                    DocumentMetadata::class.qualifiedName
                )
            )
    }

/**
 * Find attribute related to property
 *
 * @receiver KMutableProperty1<T, *>
 * @return DocumentMetadata.Attribute
 */
inline fun <reified T : Document> KMutableProperty1<T, *>.toAttribute(): DocumentMetadata.Attribute =
    T::class.documentMetadata.attributes.getValue(this.name)
