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

package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
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
                    throw AppConfigException(
                        badPlace(this),
                        message("message.error.document.attribute.reader_cannot_create")
                    )
                }
                reader
            } else {
                defaultReaders.keys.find { this.type.isSubtypeOf(it) }
                    ?.let { defaultReaders[it] }
                    ?: throw AppConfigException(
                        badPlace(this),
                        message("message.error.document.attribute.no_default_reader", this.typeName)
                    )
            }
        }
    }
