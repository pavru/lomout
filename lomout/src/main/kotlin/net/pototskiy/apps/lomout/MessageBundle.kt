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

package net.pototskiy.apps.lomout

import net.pototskiy.apps.lomout.api.UTF8Control
import org.jetbrains.annotations.PropertyKey
import java.util.*

internal object MessageBundle {
    private val bundle = ResourceBundle.getBundle("messages", UTF8Control())

    @Suppress("SpreadOperator")
    @JvmStatic
    fun message(
        @PropertyKey(resourceBundle = "messages") key: String,
        vararg params: Any?
    ): String {
        return try {
            (bundle.getString(key) as String).let {
                if (params.isNotEmpty()) {
                    it.format(*params)
                } else {
                    it
                }
            }
        } catch (e: MissingResourceException) {
            "!$key!"
        }
    }
}
