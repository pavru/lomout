package net.pototskiy.apps.magemediation.api.entity.reader

import net.pototskiy.apps.magemediation.api.AppCellDataException
import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.BooleanListType
import net.pototskiy.apps.magemediation.api.entity.BooleanType
import net.pototskiy.apps.magemediation.api.entity.values.stringToBoolean
import net.pototskiy.apps.magemediation.api.plugable.AttributeReaderPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import net.pototskiy.apps.magemediation.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat

open class BooleanListAttributeReader : AttributeReaderPlugin<BooleanListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: Attribute<out BooleanListType>, input: Cell): BooleanListType? =
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
                        .map { BooleanType(it.stringToBoolean(locale.createLocale())) }
                }
                BooleanListType(listValue)
            }
            CellType.BLANK -> null
            else -> throw AppCellDataException(
                "Reading Boolean from cell type<${input.cellType}}> is not supported, " +
                        "attribute<${attribute.name}:${attribute.valueType.simpleName}>"
            )
        }
}
