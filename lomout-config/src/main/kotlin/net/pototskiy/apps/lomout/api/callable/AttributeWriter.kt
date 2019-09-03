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

import net.pototskiy.apps.lomout.api.source.workbook.Cell
import kotlin.reflect.full.createInstance

/**
 * Base class for any attribute writer plugins
 *
 * @param T The attribute value to write to cell
 */
abstract class AttributeWriter<T : Any?> : Callable() {
    /**
     * Writer function
     *
     * @param value T? The value to write
     * @param cell Cell The cell to write value
     */
    abstract fun write(value: T, cell: Cell)
}

/**
 * Create attribute writer and apply parameters
 *
 * @param parameter Parameters set block
 * @return W
 */
inline fun <reified W : AttributeWriter<*>> createWriter(parameter: W.() -> Unit = {}): W {
    return W::class.createInstance().apply(parameter)
}
