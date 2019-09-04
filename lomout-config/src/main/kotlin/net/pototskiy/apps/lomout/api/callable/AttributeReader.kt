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

package net.pototskiy.apps.lomout.api.callable

import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import kotlin.reflect.full.createInstance

/**
 * Base class for any attribute reader plugins
 *
 * @param T The value type to return
 */
abstract class AttributeReader<T : Any?> {
    /**
     * Reader function
     *
     * @param attribute Attribute<out T> The attribute to read
     * @param input Cell The cell to read attribute value
     * @return T? The read value
     */
    abstract operator fun invoke(
        attribute: DocumentMetadata.Attribute,
        input: Cell,
        context: LomoutContext = LomoutContext.getContext()
    ): T
}

/**
 * Create attribute reader and apply parameters
 *
 * @param parameters Parameters set block
 * @return R
 */
inline fun <reified R : AttributeReader<*>> createReader(parameters: R.() -> Unit = {}): R {
    return R::class.createInstance().apply(parameters)
}
