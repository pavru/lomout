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
import java.text.ParseException
import java.util.*

/**
 * Convert string to boolean according to locale
 *
 * @receiver The boolean value as string
 * @param locale The value locale, supported: en_US, ru_RU
 * @return Value
 * @throws ParseException String cannot be parsed to boolean
 */
fun String.stringToBoolean(locale: Locale): Boolean {
    val v = this.toLowerCase().trim()
    val booleanString = stringBooleanMap[locale]?.booleanString
        ?: defaultMap.booleanString
    val booleanTrueString = stringBooleanMap[locale]?.booleanTrueString
        ?: defaultMap.booleanTrueString
    return if (v in booleanString)
        v in booleanTrueString
    else
        throw ParseException(message("message.error.data.string.to_boolean_error", this), 0)
}

private val stringBooleanMap = mapOf(
    Locale("en", "US") to BooleanStrings(
        booleanString = listOf("1", "yes", "true", "0", "no", "false"),
        booleanTrueString = listOf("1", "yes", "true")
    ),
    Locale("ru", "RU") to BooleanStrings(
        booleanString = listOf("1", "да", "истина", "0", "нет", "ложь"),
        booleanTrueString = listOf("1", "да", "истина")
    )
)

private val defaultMap = stringBooleanMap[Locale.getDefault()]
    ?: stringBooleanMap.getValue(Locale("en", "US"))

private class BooleanStrings(val booleanString: List<String>, val booleanTrueString: List<String>)
