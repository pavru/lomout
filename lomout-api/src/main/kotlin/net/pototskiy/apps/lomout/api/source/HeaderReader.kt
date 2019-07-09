package net.pototskiy.apps.lomout.api.source

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.config.loader.SourceData
import net.pototskiy.apps.lomout.api.config.loader.SourceDataCollection
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import net.pototskiy.apps.lomout.api.unknownPlace

/**
 * Read headers from source data files
 *
 * If headers are read from several sources they will be validated for compatibility
 *
 * @param sources SourceDataCollection The sources
 * @param headerRow Int The row index of headers, zero base
 * @return List<Field> The list of source fields are created based on headers
 */
fun readFieldNamesFromSource(sources: SourceDataCollection, headerRow: Int): List<Field> {
    val fieldSets = sources.map { source -> readHeaders(source, headerRow) }
    validateAllSourcesCompatible(fieldSets)
    return fieldSets.first()
}

private fun readHeaders(
    source: SourceData,
    headerRow: Int
): List<Field> {
    return WorkbookFactory.create(source.file.file.toURI().toURL()).use { workbook ->
        workbook.filter { source.sheet.isMatch(it.name) }.map { sheet ->
            sheet[headerRow]?.mapIndexed { c, cell ->
                if (cell == null || cell.cellType != CellType.STRING) {
                    throw AppDataException(
                        badPlace(sheet).let { if (cell != null) it + cell else it },
                        message("message.error.source.header.cell_is_null_or_not_string")
                    )
                }
                Field(cell.stringValue, c, null)
            } ?: throw AppDataException(badPlace(sheet), message("message.error.source.header.has_no_row"))
        }.flatten()
    }
}

private fun validateAllSourcesCompatible(fieldSets: List<List<Field>>) {
    val fieldSetSizes = fieldSets.groupBy { it.size }
    if (fieldSetSizes.keys.size > 1) {
        throw AppConfigException(unknownPlace(), message("message.error.source.header.different_field_number"))
    }
    val numberOfCombinations = fieldSetSizes.values.first().size
    val fieldSetNameColumn = fieldSets.flatten().groupBy { Pair(it.name, it.column) }
    if (fieldSetNameColumn.values.any { it.size != numberOfCombinations }) {
        throw AppConfigException(unknownPlace(), message("message.error.source.header.different_field_column"))
    }
}
