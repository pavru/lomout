package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.values.dateToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream
import java.time.LocalDate

/**
 * Default writer for **List&lt;LocalDate&gt;** attribute
 *
 * @property locale String The date locale, default system locale. This is parameter
 * @property pattern String? The date pattern, optional. This is parameter
 * @property quote Char? The value quotes. This is parameter
 * @property delimiter Char The list delimiter, default:','. This is parameter
 */
open class DateListAttributeStringWriter : AttributeWriter<List<LocalDate>?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(value: List<LocalDate>?, cell: Cell) {
        value?.let { list ->
            val listValue = ByteArrayOutputStream().use { stream ->
                stream.writer().use { writer ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .print(writer)
                        .printRecord(list.map { data ->
                            pattern?.let { data.dateToString(it) }
                                ?: data.dateToString(locale.createLocale())
                        })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
