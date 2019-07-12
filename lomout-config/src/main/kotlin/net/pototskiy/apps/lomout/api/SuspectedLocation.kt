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

package net.pototskiy.apps.lomout.api

import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import kotlin.reflect.KClass

/**
 * Domain exception suspectedLocation.
 *
 * Problem suspectedLocation code:
 * * E — Entity type
 * * A — Attribute
 * * F — Field
 * * W — Workbook
 * * S — Sheet
 * * R — Row
 * * C — Cell
 * * V — Value
 *
 * @property entity The entity with problem
 * @property attribute The attribute with problem
 * @property field The problem field
 * @property workbook The problem workbook
 * @property sheet The problem workbook sheet
 * @property row The problem workbook row
 * @property cell The problem workbook cell
 * @property data The problem value
 * @constructor
 */
data class SuspectedLocation(
    val entity: KClass<out Document>? = null,
    val attribute: DocumentMetadata.Attribute? = null,
    val field: Field? = null,
    val workbook: Workbook? = null,
    val sheet: Sheet? = null,
    val row: Row? = null,
    val cell: Cell? = null,
    val data: Any? = null
) {
    /**
     * Generate attribute text info
     *
     * @return String
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun describeAttribute(): String {
        val entityType = entity?.simpleName
            ?: attribute?.owner?.simpleName
            ?: ""
        val result = mutableListOf<String>()
        if (attribute?.name != null) result.add(message("message.exception.location.attribute", attribute.name))
        if (entityType.isNotEmpty()) result.add(message("message.exception.location.entity_type", entityType))
        return if (result.size > 0) {
            result.joinToString(", ")
        } else {
            ""
        }
    }

    /**
     * Generate cell text info
     *
     * @return String
     */
    @Suppress("MemberVisibilityCanBePrivate", "ComplexMethod")
    fun describeCell(): String {
        val workbookName = describeWorkbook()
        val sheetName = describeSheet()
        val rowNum = rowNum()
        val rowNumString = if (rowNum == null) "" else (rowNum + 1).toString()
        val columnNum = cell?.address?.column
        val column = if (columnNum == null) "" else "${columnNum + 1}(${columnNumberToAlpha(columnNum)})"
        val result = mutableListOf<String>()
        if (workbookName.isNotEmpty()) result.add(message("message.exception.location.workbook", workbookName))
        if (sheetName.isNotEmpty()) result.add(message("message.exception.location.sheet", sheetName))
        if (rowNumString.isNotEmpty()) result.add(message("message.exception.location.row", rowNumString))
        if (column.isNotEmpty()) result.add(message("message.exception.location.column", column))
        return if (result.size > 0) {
            result.joinToString(", ")
        } else {
            ""
        }
    }

    private fun rowNum() = row?.rowNum ?: cell?.row?.rowNum

    private fun describeSheet(): String {
        return (sheet?.name
            ?: row?.sheet?.name
            ?: cell?.row?.sheet?.name
            ?: "")
    }

    private fun describeWorkbook(): String {
        return (workbook?.name
            ?: sheet?.workbook?.name
            ?: row?.sheet?.workbook?.name
            ?: cell?.row?.sheet?.workbook?.name
            ?: "")
    }

    /**
     * Generate field text info
     *
     * @return String
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun describeField(): String {
        val column = if (field?.column == null) {
            ""
        } else {
            "${field.column + 1}(${columnNumberToAlpha(field.column)})"
        }
        return if (column.isNotEmpty()) {
            message("message.exception.location.field", "${field?.name ?: ""}($column)")
        } else {
            ""
        }
    }

    /**
     * Generate value text info
     *
     * @return String
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun describeValue(): String {
        return if (data == null) "" else message("message.exception.location.value", "$data(${data::class.simpleName})")
    }

    /**
     * Generate full suspectedLocation text info
     *
     * @return String
     */
    fun describeLocation(): String {
        val attrInfo = describeAttribute()
        val cellInfo = describeCell()
        val fieldInfo = describeField()
        val dataInfo = describeValue()
        val result = mutableListOf<String>()
        if (attrInfo.isNotEmpty()) result.add(attrInfo)
        if (cellInfo.isNotEmpty()) result.add(cellInfo)
        if (fieldInfo.isNotEmpty()) result.add(fieldInfo)
        if (dataInfo.isNotEmpty()) result.add(dataInfo)
        return if (result.size > 0) {
            message("message.exception.location.location", result.joinToString(", "))
        } else {
            ""
        }
    }
}

/**
 * Create [SuspectedLocation] for bad entity type
 *
 * @param entity The entity type
 * @return SuspectedLocation
 */
fun suspectedLocation(entity: KClass<out Document>) = SuspectedLocation(entity = entity)

/**
 * Create [SuspectedLocation] for a bad attribute
 *
 * @param attribute The bad attribute
 * @return SuspectedLocation
 */
fun suspectedLocation(attribute: DocumentMetadata.Attribute) = SuspectedLocation(attribute = attribute)

/**
 * Create [SuspectedLocation] for a bad field
 *
 * @param field The bad field
 * @return SuspectedLocation
 */
fun suspectedLocation(field: Field) = SuspectedLocation(field = field)

/**
 * Create [SuspectedLocation] for a bad workbook
 *
 * @param workbook The bad workbook
 * @return SuspectedLocation
 */
fun suspectedLocation(workbook: Workbook) = SuspectedLocation(workbook = workbook)

/**
 * Create [SuspectedLocation] for a bad sheet
 *
 * @param sheet The bad sheet
 * @return SuspectedLocation
 */
fun suspectedLocation(sheet: Sheet) = SuspectedLocation(sheet = sheet)

/**
 * Create [SuspectedLocation] for a bad row
 *
 * @param row The bad workbook row
 * @return SuspectedLocation
 */
fun suspectedLocation(row: Row) = SuspectedLocation(row = row)

/**
 * Create [SuspectedLocation] for a bad cell
 *
 * @param cell The bad workbook cell
 * @return SuspectedLocation
 */
fun suspectedLocation(cell: Cell) = SuspectedLocation(cell = cell)

/**
 * Create [SuspectedLocation] for bad value
 *
 * @param data The bad value
 * @return SuspectedLocation
 */
fun suspectedValue(data: Any) = SuspectedLocation(data = data)

/**
 * Create [SuspectedLocation] without any suspectedLocation
 *
 * @return SuspectedLocation
 */
fun suspectedLocation() = SuspectedLocation()

/**
 * Add bad entity type to the [SuspectedLocation]
 *
 * @receiver SuspectedLocation
 * @param entity The entity type
 * @return SuspectedLocation
 */
operator fun SuspectedLocation.plus(entity: KClass<out Document>) = this.copy(entity = entity)

/**
 * Add a bad attribute to the [SuspectedLocation]
 *
 * @receiver SuspectedLocation
 * @param attribute The bad attribute
 * @return SuspectedLocation
 */
operator fun SuspectedLocation.plus(attribute: DocumentMetadata.Attribute) = this.copy(attribute = attribute)

/**
 * Add a bad field to the [SuspectedLocation]
 *
 * @receiver SuspectedLocation
 * @param field The bad field
 * @return SuspectedLocation
 */
operator fun SuspectedLocation.plus(field: Field) = this.copy(field = field)

/**
 * Add a bad workbook to the [SuspectedLocation]
 *
 * @receiver SuspectedLocation
 * @param workbook The bad workbook
 * @return SuspectedLocation
 */
operator fun SuspectedLocation.plus(workbook: Workbook) = this.copy(workbook = workbook)

/**
 * Add a bad sheet to the [SuspectedLocation]
 *
 * @receiver SuspectedLocation
 * @param sheet The bad sheet
 * @return SuspectedLocation
 */
operator fun SuspectedLocation.plus(sheet: Sheet) = this.copy(sheet = sheet)

/**
 * Add a bad row to the [SuspectedLocation]
 *
 * @receiver SuspectedLocation
 * @param row The bad workbook row
 * @return SuspectedLocation
 */
operator fun SuspectedLocation.plus(row: Row) = this.copy(row = row)

/**
 * Add a bad cell to the [SuspectedLocation]
 *
 * @receiver SuspectedLocation
 * @param cell The bad workbook cell
 * @return SuspectedLocation
 */
operator fun SuspectedLocation.plus(cell: Cell) = this.copy(cell = cell)

/**
 * Add bad value to the [SuspectedLocation]
 *
 * @receiver SuspectedLocation
 * @param data The bad value
 * @return SuspectedLocation
 */
operator fun SuspectedLocation.plus(data: Any) = this.copy(data = data)

/**
 * Convert column number to column alpha name
 *
 * @param column The column number
 * @return String The column alpha code
 */
@Suppress("MagicNumber")
private fun columnNumberToAlpha(column: Int): String {
    var c = column
    val alpha = StringBuilder("")
    while (c >= 0) {
        alpha.append((c % 26 + 'A'.toInt()).toChar())
        c -= 26
    }
    return alpha.toString()
}
