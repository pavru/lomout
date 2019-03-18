package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppCellDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.DateListType
import net.pototskiy.apps.lomout.api.entity.DateType
import net.pototskiy.apps.lomout.api.entity.values.stringToDate
import net.pototskiy.apps.lomout.api.entity.values.stringToDateTime
import net.pototskiy.apps.lomout.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

open class DateListAttributeReader : AttributeReaderPlugin<DateListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out DateListType>, input: Cell): DateListType? {
        return when (input.cellType) {
            CellType.STRING -> {
                val listValue = input.stringValue.reader().use { reader ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .parse(reader)
                        .records
                        .map { it.toList() }.flatten()
                        .map { data ->
                            DateType(pattern?.let { data.stringToDateTime(it) }
                                ?: data.stringToDate(locale.createLocale()))
                        }
                }
                DateListType(listValue)
            }
            CellType.BLANK -> null
            else -> throw AppCellDataException(
                "Reading Date list from cell type<${input.cellType}> is not supported, " +
                        "attribute<${attribute.name}:${attribute.valueType.simpleName}>"
            )
        }
    }
}
