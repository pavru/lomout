package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.entity.StringListType
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

/**
 * Default writer for [StringListType] attribute
 *
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter, default:,
 */
open class StringListAttributeStringWriter : AttributeWriterPlugin<StringListType>() {
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(
        value: StringListType?,
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
                        .printRecord(list.map { it.value })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
