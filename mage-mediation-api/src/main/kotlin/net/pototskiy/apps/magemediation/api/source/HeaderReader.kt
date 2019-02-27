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
                    throw ConfigException("Header row in source<${workbook.name}:${sheet.name}> has no cell or cell has not string type in column ${c + 1}")
                }
                Field(cell.stringValue, c, null, null)
            } ?: throw ConfigException("Source<${workbook.name}:${sheet.name}> has no header row")
        }.flatten()
    }
}

private fun validateAllSourcesCompatible(fieldSets: List<List<Field>>) {
    fieldSets.forEach { first ->
        fieldSets.forEach { second ->
            if (first.size != second.size) {
                throw ConfigException("Sources have different number of fields")
            }
            first.forEach { (name, column) ->
                if (!second.any { it.name == name && it.column == column }) {
                    throw ConfigException("Sources have different fields or fields in different columns")
                }
            }
        }
    }
}
