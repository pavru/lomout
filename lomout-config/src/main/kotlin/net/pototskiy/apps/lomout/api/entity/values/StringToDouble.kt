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

package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.MessageBundle.message
import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition
import java.util.*

/**
 * Convert String to Double according to the locale
 *
 * @receiver String
 * @param locale Locale
 * @return Double?
 * @throws java.text.ParseException
 */
fun String.stringToDouble(locale: Locale, groupingUsed: Boolean): Double {
    val format = NumberFormat.getInstance(locale).apply {
        isGroupingUsed = groupingUsed
    }
    val position = ParsePosition(0)
    val value = format.parse(this.trim(), position)
        ?: throw ParseException(message("message.error.data.string.to_double_error"), position.index)
    if (position.index != this.trim().length) {
        throw ParseException(message("message.error.data.string.to_double_extra"), position.index)
    }
    return value.toDouble()
}

/**
 * Convert Double to String according to the locale
 *
 * @receiver Double
 * @param locale Locale
 * @return String
 */
fun Double.doubleToString(locale: Locale): String {
    val format = NumberFormat.getNumberInstance(locale)
    return format.format(this)
}
