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

@file:Suppress("TooManyFunctions")

package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.badData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.*

/**
 * Convert string value to [LocalDate] (only date part) according to locale
 *
 * @receiver The string to convert
 * @param locale The locale for conversion
 * @return Value
 * @throws AppDataException The string cannot be converted to [LocalDate]
 */
fun String.stringToDate(locale: Locale): LocalDate {
    val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)
    return try {
        LocalDate.parse(this.trim(), format)
    } catch (e: DateTimeParseException) {
        throw AppDataException(
            badData(this),
            message("message.error.data.string.to_date_locale_error", this, locale),
            e
        )
    }
}

/**
 * Convert string to [LocalDate] according to pattern.
 *
 * @receiver String
 * @param pattern String
 * @return DateTime
 * @throws AppDataException The string cannot be converted to [LocalDate]
 */
fun String.stringToDate(pattern: String): LocalDate {
    val format = DateTimeFormatter.ofPattern(pattern)
    return try {
        LocalDate.parse(this.trim(), format)
    } catch (e: DateTimeParseException) {
        throw AppDataException(
            badData(this),
            message("message.error.data.string.to_date_pattern_error", this, pattern),
            e
        )
    }
}

/**
 * Convert string to [LocalDateTime] according to pattern.
 *
 * @receiver String
 * @param pattern String
 * @return DateTime
 * @throws AppDataException The string cannot be converted to [LocalDateTime]
 */
fun String.stringToDateTime(pattern: String): LocalDateTime {
    val format = DateTimeFormatter.ofPattern(pattern)
    return try {
        LocalDateTime.parse(this.trim(), format)
    } catch (e: DateTimeParseException) {
        throw AppDataException(
            badData(this),
            message("message.error.data.string.to_datetime_pattern_error", this, pattern),
            e
        )
    }
}

/**
 * Convert string to [LocalDateTime] according to locale
 *
 * @receiver String The string to convert
 * @param locale Locale The locale for conversion
 * @return DateTime
 * @throws AppDataException The string can be converted
 */
fun String.stringToDateTime(locale: Locale): LocalDateTime {
    val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale)
    return try {
        LocalDateTime.parse(this.trim(), format)
    } catch (e: DateTimeParseException) {
        throw AppDataException(
            badData(this),
            message("message.error.data.string.to_datetime_locale_error", this, locale),
            e
        )
    }
}

/**
 * Convert [LocalDate] (date part) to string according to locale
 *
 * @receiver DateTime The value to convert
 * @param locale Locale The locale for conversion
 * @return String
 */
fun LocalDate.dateToString(locale: Locale): String =
    this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale))

/**
 * Convert [LocalDateTime] to string according to locale
 *
 * @receiver DateTime The value to convert
 * @param locale Locale The locale for conversion
 * @return String
 */
@PublicApi
fun LocalDateTime.datetimeToString(locale: Locale): String =
    this.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale))

/**
 * Convert [LocalDateTime] to string according to pattern
 *
 * @receiver DateTime The value to convert
 * @param pattern String The pattern for conversion
 * @return String
 */
fun LocalDateTime.datetimeToString(pattern: String): String = this.format(DateTimeFormatter.ofPattern(pattern))

/**
 * Convert [LocalDate] to string according to pattern
 *
 * @receiver DateTime The value to convert
 * @param pattern String The pattern for conversion
 * @return String
 */
fun LocalDate.dateToString(pattern: String): String = this.format(DateTimeFormatter.ofPattern(pattern))
