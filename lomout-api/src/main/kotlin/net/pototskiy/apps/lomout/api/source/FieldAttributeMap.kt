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

package net.pototskiy.apps.lomout.api.source

import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.AttributeCollection

/**
 * Source field to attribute map
 *
 * @property fieldToAttr Map<Field, Attribute<out Type>>
 * @property fields FieldCollection
 * @property attributes AttributeCollection
 * @constructor
 */
data class FieldAttributeMap(private val fieldToAttr: Map<Field, DocumentMetadata.Attribute>) :
    Map<Field, DocumentMetadata.Attribute> by fieldToAttr {
    /**
     * Fields
     */
    val fields: FieldCollection
        get() = FieldCollection(fieldToAttr.keys.toList())
    /**
     * Attributes
     */
    val attributes: AttributeCollection
        get() = AttributeCollection(fieldToAttr.values.toList())
}
