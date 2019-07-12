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
import net.pototskiy.apps.lomout.api.suspectedLocation
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
                        suspectedLocation(this),
                        message("message.error.document.attribute.writer_cannot_create")
                    )
                }
                writer
            } else {
                defaultWriters.keys.find { this.type.isSubtypeOf(it) }
                    ?.let { defaultWriters[it] }
                    ?: throw AppConfigException(
                        suspectedLocation(this),
                        message("message.error.document.attribute.no_default_writer", this.typeName)
                    )
            }
        }
    }
