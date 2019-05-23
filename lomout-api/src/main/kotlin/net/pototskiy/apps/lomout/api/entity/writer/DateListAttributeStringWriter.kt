package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.DateListType
import net.pototskiy.apps.lomout.api.entity.values.dateToString
import net.pototskiy.apps.lomout.api.entity.values.datetimeToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

/**
 * Default writer for [DateListType] attribute
 *
 * @property locale String The date locale, default system locale
 * @property pattern String? The date pattern, optional
 * @property quote Char? The value quote
 * @property delimiter Char The list delimiter, default:,
 */
open class DateListAttributeStringWriter : AttributeWriterPlugin<DateListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(
        value: DateListType?,
        cell: Cell
    ) {
        value?.let { list ->
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
