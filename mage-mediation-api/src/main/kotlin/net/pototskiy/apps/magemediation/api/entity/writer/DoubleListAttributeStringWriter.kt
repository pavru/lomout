package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleListValue
import net.pototskiy.apps.magemediation.api.entity.values.doubleToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

open class DoubleListAttributeStringWriter : AttributeWriterPlugin<DoubleListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(
        value: DoubleListType?,
        cell: Cell
    ) {
        (value as? DoubleListValue)?.let { list ->
            val listValue = ByteArrayOutputStream().use { stream ->
                stream.writer().use { writer ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .print(writer)
                        .printRecord(list.map { it.value.doubleToString(locale.createLocale()) })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
