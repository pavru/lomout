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

import kotlin.reflect.KMutableProperty1

/**
 * Document attributes map.
 *
 * @property data Data map
 */
class DocumentData(private val data: MutableMap<DocumentMetadata.Attribute, Any>) :
    MutableMap<DocumentMetadata.Attribute, Any> by data {
    /**
     * Get attribute value.
     *
     * @param property The document property(attribute).
     */
    operator fun get(property: KMutableProperty1<out Document, *>): Any? = data[property.toAttribute()]

    /**
     * Set attribute value.
     *
     * @param property The document property(attribute).
     * @param value The attribute's value.
     */
    operator fun set(property: KMutableProperty1<out Document, *>, value: Any) {
        data[property.toAttribute()] = value
    }

    /**
     * Get attribute value or default value if no attribute in the map.
     *
     * @param property The document property(attribute).
     * @param defaultValue The default attribute value.
     */
    @Suppress("unused")
    fun getOrDefault(property: KMutableProperty1<out Document, *>, defaultValue: Any): Any =
        data.getOrDefault(property.toAttribute(), defaultValue)

    /**
     * Check if attribute in the map.
     *
     * @param property The document property(attribute).
     */
    @Suppress("unused")
    fun containsKey(property: KMutableProperty1<out Document, *>): Boolean =
        data.containsKey(property.toAttribute())
}

/**
 * Empty document map.
 */
fun emptyDocumentData() = DocumentData(mutableMapOf())

/**
 * Create document map from pairs of attribute and value.
 *
 * @param pairs Pairs of attribute and value.
 */
@Suppress("unused")
fun documentData(vararg pairs: Pair<DocumentMetadata.Attribute, Any>): DocumentData =
    if (pairs.isNotEmpty()) DocumentData(pairs.toMap().toMutableMap()) else emptyDocumentData()

/**
 * Convert list of attribute-value pairs to document map.
 *
 * @receiver The list of attribute-value pairs.
 */
fun List<Pair<DocumentMetadata.Attribute, Any>>.toDocumentData(): DocumentData =
    DocumentData(this.toMap().toMutableMap())
