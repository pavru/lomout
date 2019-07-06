package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.plugable.AttributeWriter
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

/**
 * Default writer for **List&lt;String&gt;** attribute
 *
 * @property quote Char? The value quote, optional. This is parameter
 * @property delimiter Char The list delimiter, default:','. This is parameter
 */
open class StringListAttributeStringWriter : AttributeWriter<List<String>?>() {
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(value: List<String>?, cell: Cell) {
        value?.let { list ->
            val listValue = ByteArrayOutputStream().use { stream ->
                stream.writer().use { writer ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .print(writer)
                        .printRecord(list.map { it })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
