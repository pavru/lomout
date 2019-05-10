package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.entity.BooleanListType
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

/**
 * Default writer for [BooleanListType] attribute
 *
 * * true -> 1
 * * false -> 0
 *
 * @property locale String The value locale, ignored
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter, default: ,
 */
open class BooleanListAttributeStringWriter : AttributeWriterPlugin<BooleanListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(
        value: BooleanListType?,
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
                        .printRecord(list.map { if (it.value) "1" else "0" })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
