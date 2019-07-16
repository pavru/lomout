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

package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.values.doubleToLong
import net.pototskiy.apps.lomout.api.entity.values.doubleToString
import net.pototskiy.apps.lomout.api.entity.values.longToString
import net.pototskiy.apps.lomout.api.entity.values.stringToBoolean
import net.pototskiy.apps.lomout.api.entity.values.stringToDate
import net.pototskiy.apps.lomout.api.entity.values.stringToDateTime
import net.pototskiy.apps.lomout.api.entity.values.stringToDouble
import net.pototskiy.apps.lomout.api.entity.values.stringToLong
import net.pototskiy.apps.lomout.api.entity.values.toLocalDate
import net.pototskiy.apps.lomout.api.entity.values.toLocalDateTime
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.suspectedLocation
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import java.text.ParseException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Read [LocalDateTime] from the cell. Convert non-DateTime values to [LocalDateTime].
 *
 * @receiver Cell The cell to read
 * @param attribute The destination attribute
 * @param locale The locale for converting
 * @return Read value
 * @throws AppDataException The string value cannot be converted to [LocalDateTime]
 */
fun Cell.readeDateTimeWithLocale(
    attribute: DocumentMetadata.Attribute,
    locale: Locale?
): LocalDateTime? = when (this.cellType) {
    CellType.LONG -> Date(this.longValue).toLocalDateTime()
    CellType.DOUBLE -> HSSFDateUtil.getJavaDate(this.doubleValue).toLocalDateTime()
    CellType.BOOL -> null
    CellType.STRING ->
        try {
            this.stringValue.stringToDateTime(locale ?: this.locale)
        } catch (e: AppDataException) {
            throw AppDataException(e.suspectedLocation + this + attribute, e.message, e)
        }
    CellType.BLANK -> null
}

/**
 * Read [LocalDateTime] value from the cell. Convert non-[LocalDateTime] values to [LocalDateTime].
 *
 * @receiver The cell to read
 * @param attribute The destination attribute
 * @param pattern The date-time pattern for converting
 * @return Read value
 * @throws AppDataException The string value cannot be converted to [LocalDateTime]
 */
fun Cell.readeDateTimeWithPattern(
    @Suppress("UNUSED_PARAMETER") attribute: DocumentMetadata.Attribute,
    pattern: String
): LocalDateTime? = when (this.cellType) {
    CellType.LONG -> Date(this.longValue).toLocalDateTime()
    CellType.DOUBLE -> HSSFDateUtil.getJavaDate(this.doubleValue).toLocalDateTime()
    CellType.BOOL -> null
    CellType.STRING ->
        try {
            this.stringValue.stringToDateTime(pattern)
        } catch (e: AppDataException) {
            throw AppDataException(e.suspectedLocation + this + attribute, e.message, e)
        }
    CellType.BLANK -> null
}

/**
 * Read [LocalDate] from the cell. Convert non-DateTime values to [LocalDate].
 *
 * @receiver Cell The cell to read
 * @param attribute The destination attribute
 * @param locale The locale for converting
 * @return Read value
 * @throws AppDataException The string value cannot be converted to [LocalDate]
 */
fun Cell.readDateWithLocale(
    attribute: DocumentMetadata.Attribute,
    locale: Locale?
): LocalDate? = when (this.cellType) {
    CellType.LONG -> Date(this.longValue).toLocalDate()
    CellType.DOUBLE -> HSSFDateUtil.getJavaDate(this.doubleValue).toLocalDate()
    CellType.BOOL -> null
    CellType.STRING ->
        try {
            this.stringValue.stringToDate(locale ?: this.locale)
        } catch (e: AppDataException) {
            throw AppDataException(e.suspectedLocation + this + attribute, e.message, e)
        }
    CellType.BLANK -> null
}

/**
 * Read [LocalDate] value from the cell. Convert non-[LocalDate] values to [LocalDate].
 *
 * @receiver The cell to read
 * @param attribute The destination attribute
 * @param pattern The date-time pattern for converting
 * @return Read value
 * @throws AppDataException The string value cannot be converted to [LocalDate]
 */
fun Cell.readDateWithPattern(
    @Suppress("UNUSED_PARAMETER") attribute: DocumentMetadata.Attribute,
    pattern: String
): LocalDate? = when (this.cellType) {
    CellType.LONG -> Date(this.longValue).toLocalDateTime().toLocalDate()
    CellType.DOUBLE -> HSSFDateUtil.getJavaDate(this.doubleValue).toLocalDate()
    CellType.BOOL -> null
    CellType.STRING ->
        try {
            this.stringValue.stringToDate(pattern)
        } catch (e: AppDataException) {
            throw AppDataException(e.suspectedLocation + this + attribute, e.message, e)
        }
    CellType.BLANK -> null
}

/**
 * Read Boolean value from the cell. Convert non-Boolean values to Boolean.
 *
 * @receiver The cell to read
 * @param locale The locale for converting
 * @return The value
 */
fun Cell.readBoolean(locale: Locale?): Boolean? = when (this.cellType) {
    CellType.LONG -> this.longValue != 0L
    CellType.DOUBLE -> this.doubleValue != 0.0
    CellType.BOOL -> this.booleanValue
    CellType.STRING -> try {
        this.stringValue.stringToBoolean(locale ?: this.locale)
    } catch (e: ParseException) {
        throw AppDataException(suspectedLocation(this), e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read Double value from the cell. Convert non-Double values to Double.
 *
 * @receiver The cell to read
 * @param locale The locale for converting
 * @return The value
 */
@Suppress("ComplexMethod")
fun Cell.readDouble(locale: Locale?, groupingUsed: Boolean): Double? = when (this.cellType) {
    CellType.LONG -> this.longValue.toDouble()
    CellType.DOUBLE -> this.doubleValue
    CellType.BOOL -> if (this.booleanValue) 1.0 else 0.0
    CellType.STRING -> try {
        this.stringValue.stringToDouble(locale ?: this.locale, groupingUsed)
    } catch (e: ParseException) {
        throw AppDataException(suspectedLocation(this), e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read Long value from the cell. Convert non-Long values to Long when necessary.
 *
 * @receiver Cell The cell to read
 * @param locale Locale The locale for converting
 * @return Long?
 */
@Suppress("ComplexMethod")
fun Cell.readLong(locale: Locale?, groupingUsed: Boolean): Long? = when (this.cellType) {
    CellType.LONG -> this.longValue
    CellType.DOUBLE -> this.doubleValue.doubleToLong()
    CellType.BOOL -> if (this.booleanValue) 1L else 0L
    CellType.STRING -> try {
        this.stringValue.stringToLong(locale ?: this.locale, groupingUsed)
    } catch (e: ParseException) {
        throw AppDataException(suspectedLocation(this), e.message, e)
    }
    CellType.BLANK -> null
}

/**
 * Read string from the cell, non-string values converted to string according to locale
 *
 * @receiver Cell The cell to read
 * @param locale Locale The locale for converting
 * @return String?
 */
fun Cell.readString(locale: Locale?, groupingUsed: Boolean): String? = when (this.cellType) {
    CellType.LONG -> this.longValue.longToString(locale ?: this.locale,groupingUsed)
    CellType.DOUBLE -> this.doubleValue.doubleToString(locale ?: this.locale, groupingUsed)
    CellType.BOOL -> if (this.booleanValue) "1" else "0"
    CellType.STRING -> this.stringValue
    CellType.BLANK -> null
}
