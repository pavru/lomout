package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.values.stringToDouble
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginException
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

open class DoubleListAttributeReader : AttributeReaderPlugin<DoubleListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out DoubleListType>, input: Cell): DoubleListType? =
        when (input.cellType) {
            CellType.STRING -> {
                val listValue = input.stringValue.reader().use { reader ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .parse(reader)
                        .records
                        .map { it.toList() }.flatten()
                        .map { DoubleType(it.stringToDouble(locale.createLocale())) }
                }
                DoubleListType(listValue)
            }
            CellType.BLANK -> null
            else -> throw PluginException(
                "Reading Double from cell type<${input.cellType}> is not supported, " +
                        "attribute<${attribute.name}:${attribute.valueType.simpleName}>"
            )
        }
}
