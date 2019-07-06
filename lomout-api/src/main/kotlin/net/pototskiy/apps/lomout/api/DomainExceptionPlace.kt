@file:Suppress("TooManyFunctions")

package net.pototskiy.apps.lomout.api

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import net.pototskiy.apps.lomout.api.source.workbook.Workbook
import kotlin.reflect.KClass

/**
 * Domain exception place.
 *
 * Problem place code:
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
data class DomainExceptionPlace(
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
    fun attributeInfo(): String {
        val entityType = entity?.simpleName
            ?: attribute?.owner?.simpleName
            ?: ""
        val result = mutableListOf<String>()
        if (attribute?.name != null) result.add("A:'${attribute.name}'")
        if (entityType.isNotEmpty()) result.add("E:'$entityType'")
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
    fun cellInfo(): String {
        val workbookName = workbookName()
        val sheetName = sheetName()
        val rowNum = rowNum()
        val rowNumString = if (rowNum == null) "" else (rowNum + 1).toString()
        val columnNum = cell?.address?.column
        val column = if (columnNum == null) "" else "${columnNum + 1}(${columnNumberToAlpha(columnNum)})"
        val result = mutableListOf<String>()
        if (workbookName.isNotEmpty()) result.add("W:'$workbookName'")
        if (sheetName.isNotEmpty()) result.add("S:'$sheetName'")
        if (rowNumString.isNotEmpty()) result.add("R:'$rowNumString'")
        if (column.isNotEmpty()) result.add("C:'$column'")
        return if (result.size > 0) {
            result.joinToString(", ")
        } else {
            ""
        }
    }

    private fun rowNum() = row?.rowNum ?: cell?.row?.rowNum

    private fun sheetName(): String {
        return (sheet?.name
            ?: row?.sheet?.name
            ?: cell?.row?.sheet?.name
            ?: "")
    }

    private fun workbookName(): String {
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
    fun fieldInfo(): String {
        val column = if (field?.column == null) {
            ""
        } else {
            "${field.column + 1}(${columnNumberToAlpha(field.column)})"
        }
        return if (column.isNotEmpty()) {
            "F:'${field?.name ?: ""}($column)'"
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
    fun dataInfo(): String {
        return if (data == null) "" else "V:'$data(${data::class.simpleName})'"
    }

    /**
     * Generate full place text info
     *
     * @return String
     */
    fun placeInfo(): String {
        val attrInfo = attributeInfo()
        val cellInfo = cellInfo()
        val fieldInfo = fieldInfo()
        val dataInfo = dataInfo()
        val result = mutableListOf<String>()
        if (attrInfo.isNotEmpty()) result.add(attrInfo)
        if (cellInfo.isNotEmpty()) result.add(cellInfo)
        if (fieldInfo.isNotEmpty()) result.add(fieldInfo)
        if (dataInfo.isNotEmpty()) result.add(dataInfo)
        return if (result.size > 0) {
            "Place: ${result.joinToString(", ")}."
        } else {
            ""
        }
    }
}

/**
 * Create [DomainExceptionPlace] for bad entity type
 *
 * @param entity The entity type
 * @return DomainExceptionPlace
 */
fun badPlace(entity: KClass<out Document>) = DomainExceptionPlace(entity = entity)

/**
 * Create [DomainExceptionPlace] for a bad attribute
 *
 * @param attribute The bad attribute
 * @return DomainExceptionPlace
 */
fun badPlace(attribute: DocumentMetadata.Attribute) = DomainExceptionPlace(attribute = attribute)

/**
 * Create [DomainExceptionPlace] for a bad field
 *
 * @param field The bad field
 * @return DomainExceptionPlace
 */
fun badPlace(field: Field) = DomainExceptionPlace(field = field)

/**
 * Create [DomainExceptionPlace] for a bad workbook
 *
 * @param workbook The bad workbook
 * @return DomainExceptionPlace
 */
fun badPlace(workbook: Workbook) = DomainExceptionPlace(workbook = workbook)

/**
 * Create [DomainExceptionPlace] for a bad sheet
 *
 * @param sheet The bad sheet
 * @return DomainExceptionPlace
 */
fun badPlace(sheet: Sheet) = DomainExceptionPlace(sheet = sheet)

/**
 * Create [DomainExceptionPlace] for a bad row
 *
 * @param row The bad workbook row
 * @return DomainExceptionPlace
 */
fun badPlace(row: Row) = DomainExceptionPlace(row = row)

/**
 * Create [DomainExceptionPlace] for a bad cell
 *
 * @param cell The bad workbook cell
 * @return DomainExceptionPlace
 */
fun badPlace(cell: Cell) = DomainExceptionPlace(cell = cell)

/**
 * Create [DomainExceptionPlace] for bad value
 *
 * @param data The bad value
 * @return DomainExceptionPlace
 */
fun badData(data: Any) = DomainExceptionPlace(data = data)

/**
 * Create [DomainExceptionPlace] without any place
 *
 * @return DomainExceptionPlace
 */
fun unknownPlace() = DomainExceptionPlace()

/**
 * Add bad entity type to the [DomainExceptionPlace]
 *
 * @receiver DomainExceptionPlace
 * @param entity The entity type
 * @return DomainExceptionPlace
 */
operator fun DomainExceptionPlace.plus(entity: KClass<out Document>) = this.copy(entity = entity)

/**
 * Add a bad attribute to the [DomainExceptionPlace]
 *
 * @receiver DomainExceptionPlace
 * @param attribute The bad attribute
 * @return DomainExceptionPlace
 */
operator fun DomainExceptionPlace.plus(attribute: DocumentMetadata.Attribute) = this.copy(attribute = attribute)

/**
 * Add a bad field to the [DomainExceptionPlace]
 *
 * @receiver DomainExceptionPlace
 * @param field The bad field
 * @return DomainExceptionPlace
 */
operator fun DomainExceptionPlace.plus(field: Field) = this.copy(field = field)

/**
 * Add a bad workbook to the [DomainExceptionPlace]
 *
 * @receiver DomainExceptionPlace
 * @param workbook The bad workbook
 * @return DomainExceptionPlace
 */
operator fun DomainExceptionPlace.plus(workbook: Workbook) = this.copy(workbook = workbook)

/**
 * Add a bad sheet to the [DomainExceptionPlace]
 *
 * @receiver DomainExceptionPlace
 * @param sheet The bad sheet
 * @return DomainExceptionPlace
 */
operator fun DomainExceptionPlace.plus(sheet: Sheet) = this.copy(sheet = sheet)

/**
 * Add a bad row to the [DomainExceptionPlace]
 *
 * @receiver DomainExceptionPlace
 * @param row The bad workbook row
 * @return DomainExceptionPlace
 */
operator fun DomainExceptionPlace.plus(row: Row) = this.copy(row = row)

/**
 * Add a bad cell to the [DomainExceptionPlace]
 *
 * @receiver DomainExceptionPlace
 * @param cell The bad workbook cell
 * @return DomainExceptionPlace
 */
operator fun DomainExceptionPlace.plus(cell: Cell) = this.copy(cell = cell)

/**
 * Add bad value to the [DomainExceptionPlace]
 *
 * @receiver DomainExceptionPlace
 * @param data The bad value
 * @return DomainExceptionPlace
 */
operator fun DomainExceptionPlace.plus(data: Any) = this.copy(data = data)

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
