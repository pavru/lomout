package net.pototskiy.apps.magemediation.api.source

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.loader.SourceData
import net.pototskiy.apps.magemediation.api.config.loader.SourceDataCollection
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import net.pototskiy.apps.magemediation.api.source.workbook.WorkbookFactory

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
                    throw ConfigException(
                        "Header row in source<${workbook.name}:${sheet.name}> " +
                                "has no cell or cell has not string type in column ${c + 1}"
                    )
                }
                Field(cell.stringValue, c, null, null)
            } ?: throw ConfigException("Source<${workbook.name}:${sheet.name}> has no header row")
        }.flatten()
    }
}

private fun validateAllSourcesCompatible(fieldSets: List<List<Field>>) {
    // TODO: 02.03.2019 write test for this validate
    val fieldSetSizes = fieldSets.groupBy { it.size }
    if (fieldSetSizes.keys.size > 1) {
        throw ConfigException("Sources have different number of fields")
    }
    val fieldSetNameColumn = fieldSets.flatten().groupBy { Pair(it.name, it.column) }
    if (fieldSetNameColumn.values.any { it.size != fieldSetSizes.values.size }) {
        throw ConfigException("Sources have different fields or fields in different columns")
    }
}
