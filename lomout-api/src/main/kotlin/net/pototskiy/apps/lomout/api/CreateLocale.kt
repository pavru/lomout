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

package net.pototskiy.apps.lomout.api

import java.util.*

@Suppress("TooGenericExceptionCaught")
/**
 * Convent string presentation of locale to Local object
 *
 * @receiver String The locale in form ll_CC where CC — country code,
 *  ll — language code
 * @return Locale
 */
fun String.createLocale(): Locale {
    return try {
        val (l, c) = this.split("_")
        Locale(l, c)
    } catch (e: Exception) {
        Locale.getDefault()
    }
}
