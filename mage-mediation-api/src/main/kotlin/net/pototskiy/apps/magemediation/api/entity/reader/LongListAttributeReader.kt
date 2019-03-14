package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.LongListType
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.values.stringToLong
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.plugable.PluginException
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

open class LongListAttributeReader : AttributeReaderPlugin<LongListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out LongListType>, input: Cell): LongListType? = when (input.cellType) {
        CellType.STRING -> {
            val listValue = input.stringValue.reader().use { reader ->
                CSVFormat.RFC4180
                    .withQuote(quote)
                    .withDelimiter(delimiter)
                    .withRecordSeparator("")
                    .parse(reader)
                    .records
                    .map { it.toList() }.flatten()
                    .map { LongType(it.stringToLong(locale.createLocale())) }
            }
            LongListType(listValue)
        }
        CellType.BLANK -> null
        CellType.LONG -> LongListType(listOf(LongType(input.longValue)))
        else -> throw PluginException(
            "Reading long list from cell type<${input.cellType}> is not supported, " +
                    "attribute<${attribute.name}:${attribute.valueType.simpleName}>"
        )
    }
}
