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
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.and
import org.litote.kmongo.eq
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

/**
 * Abstract document to represent entity.
 *
 * @property documentMetadata DocumentMetadata
 * @property _id ObjectId
 * @property removed Boolean
 * @property createTime LocalDateTime
 * @property updateTime LocalDateTime
 * @property removeTime LocalDateTime?
 * @property toucheTime LocalDateTime
 * @property absentDays Int
 */
abstract class Document {
    /**
     * Document metadata
     */
    @BsonIgnore
    val documentMetadata = verifiedMetadata.getOrPut(this::class) {
        (this::class.companionObjectInstance as? DocumentMetadata)
            ?.also {
                if (it.klass != this::class) {
                    throw DocumentException(
                        message(
                            "message.error.document.wrong_metadata",
                            this::class.qualifiedName,
                            it.klass.qualifiedName
                        )
                    )
                }
            } ?: throw DocumentException(
            message(
                "message.error.document.no_companion",
                this::class.qualifiedName,
                DocumentMetadata::class.qualifiedName
            )
        )
    }
    @Suppress("PropertyName", "VariableNaming")
    @BsonId
    var _id: ObjectId = ObjectId()
    var removed: Boolean = false
    var createTime: LocalDateTime = Documents.timestamp
    var updateTime: LocalDateTime = Documents.timestamp
    var removeTime: LocalDateTime? = null
    var toucheTime: LocalDateTime = Documents.timestamp
    var absentDays: Int = 0

    /**
     * Update touch time of entity
     */
    fun touch() {
        toucheTime = Documents.timestamp
        removeTime = null
        removed = false
    }

    /**
     * Mark entity as 'updated'. Update time is changed.
     */
    fun markUpdated() {
        updateTime = Documents.timestamp
        toucheTime = Documents.timestamp
        removeTime = null
        removed = false
    }

    /**
     * Mark entity as 'removed'. Remove time is changed.
     */
    fun markRemoved() {
        removeTime = Documents.timestamp
        removed = true
    }

    /**
     * Set entity attribute value.
     *
     * @param name The attribute name
     * @param value The attribute value
     */
    fun setAttribute(name: String, value: Any?) {
        val attr = checkAndGetAttribute(name)
        checkType(attr, value)
        attr.setter.call(this, value)
    }

    /**
     * Set entity attribute value.
     *
     * @param attribute The attribute
     * @param value The attribute value
     */
    fun setAttribute(attribute: Attribute, value: Any?) = setAttribute(attribute.name, value)

    private fun checkAndGetAttribute(name: String) = (documentMetadata.attributes[name]
        ?: throw DocumentException(message("message.error.document.no_attribute", this::class.simpleName, name)))

    /**
     * Get attribute value.
     * @param name The attribute name
     * @return The attribute value
     */
    fun getAttribute(name: String): Any? {
        val attr = checkAndGetAttribute(name)
        return attr.getter.call(this)
    }

    /**
     * Get attribute value.
     *
     * @param attribute The attribute
     * @return The attribute value
     */
    fun getAttribute(attribute: Attribute): Any? = getAttribute(attribute.name)

    /**
     * Get the document key attributes filter
     *
     * @return Bson
     */
    @Suppress("SpreadOperator")
    fun keysFilter(): Bson {
        return and(*documentMetadata.keyAttributes.map { it.property eq getAttribute(it) }.toTypedArray())
    }

    @Suppress("ThrowsCount")
    private fun checkType(attr: Attribute, value: Any?) {
        if (value == null && !attr.isNullable) {
            throw DocumentException(message("message.error.document.set_null", attr.name, this::class.simpleName))
        }
        if (value != null) {
            if (!attr.klass.isInstance(value)) {
                throw DocumentException(
                    message("message.error.document.set_wrong_type", value::class.qualifiedName, attr.typeName)
                )
            }
            if (value is List<*> && value.isNotEmpty() && !attr.listParameter.isInstance(value.first())) {
                throw DocumentException(
                    message(
                        "message.error.document.set_wrong_type",
                        "${value::class.qualifiedName}<${value.first()!!::class.qualifiedName}>",
                        attr.typeName
                    )
                )
            }
        }
    }

    @get:BsonIgnore
    val documentData: DocumentData
        get() {
            return documentMetadata.attributes.values.mapNotNull { attr ->
                getAttribute(attr)?.let { attr to it }
            }.toDocumentData()
        }

    companion object {
        private val verifiedMetadata = mutableMapOf<KClass<out Document>, DocumentMetadata>()
        /**
         * Support attribute types.
         */
        val supportedTypes = SupportAttributeType
        /**
         * Empty document, document with no attributes
         */
        val emptyDocument = EmptyDocument()
    }

    class EmptyDocument: Document() {
        companion object: DocumentMetadata(EmptyDocument::class)
    }
}
