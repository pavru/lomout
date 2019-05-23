package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.DoubleListType
import net.pototskiy.apps.lomout.api.entity.values.doubleToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

/**
 * Default writer for [DoubleListType] attribute
 *
 * @property locale String The value locale
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter, default:,
 */
open class DoubleListAttributeStringWriter : AttributeWriterPlugin<DoubleListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(
        value: DoubleListType?,
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
                        .printRecord(list.map { it.value.doubleToString(locale.createLocale()) })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
