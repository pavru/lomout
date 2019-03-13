package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.DateListType
import net.pototskiy.apps.magemediation.api.entity.DateListValue
import net.pototskiy.apps.magemediation.api.entity.values.dateToString
import net.pototskiy.apps.magemediation.api.entity.values.datetimeToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

open class DateListAttributeStringWriter : AttributeWriterPlugin<DateListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(
        value: DateListType?,
        cell: Cell
    ) {
        (value as? DateListValue)?.let { list ->
            val listValue = ByteArrayOutputStream().use { stream ->
                stream.writer().use { writer ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .print(writer)
                        .printRecord(list.map { data ->
                            pattern?.let { data.value.datetimeToString(it) }
                                ?: data.value.dateToString(locale.createLocale())
                        })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
