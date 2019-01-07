package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.config.dataset.Field.Companion.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.config.dataset.FieldSetType
import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.DatasetConfiguration
import net.pototskiy.apps.magemediation.config.newOne.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.config.newOne.type.AttributeStringType
import net.pototskiy.apps.magemediation.source.Sheet

class Headers(private val sheet: Sheet, private val dataset: DatasetConfiguration) {

    fun getHeaders(): List<FieldConfiguration> {
        val confHeaders = dataset.fieldSets.find { it.type == FieldSetType.MAIN }?.fields
            ?: listOf()
        return if (dataset.headersRow != -1) {
            val sourceHeaders = mutableListOf<FieldConfiguration>()
            val headerRow = sheet[dataset.headersRow]
                ?: throw LoaderException(
                    "Workbook<${sheet.workbook.name}> " +
                            "sheet<${sheet.name}> does not have header row"
                )
            headerRow.forEachIndexed { c, cell ->
                sourceHeaders.add(
                    FieldConfiguration(
                        cell.stringValue,
                        c,
                        null,
                        AttributeStringType(false, DEFAULT_LOCALE),
                        false,
                        false,
                        null,
                        false
                    )
                )
            }
            val needUpdate = sourceHeaders.filter { field -> confHeaders.any { it.name == field.name } }
            sourceHeaders.removeIf { field -> confHeaders.any { it.name == field.name } }
            confHeaders.forEach { field ->
                if (field.nested) {
                    sourceHeaders.add(field)
                } else {
                    sourceHeaders.add(
                        FieldConfiguration(
                            field.name,
                            if (field.column == UNDEFINED_COLUMN)
                                needUpdate.find { it.name == field.name }?.column as Int
                            else
                                field.column,
                            field.regex,
                            field.type,
                            field.keyField,
                            field.nested,
                            field.parent,
                            field.optional
                        )
                    )
                }
            }
            sourceHeaders.toList()
        } else {
            confHeaders.toList()
        }
    }
}
