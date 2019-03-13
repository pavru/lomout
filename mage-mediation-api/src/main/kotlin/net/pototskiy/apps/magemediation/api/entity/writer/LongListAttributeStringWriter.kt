package net.pototskiy.apps.magemediation.api.entity.writer

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.magemediation.api.createLocale
import net.pototskiy.apps.magemediation.api.entity.LongListType
import net.pototskiy.apps.magemediation.api.entity.LongListValue
import net.pototskiy.apps.magemediation.api.entity.values.longToString
import net.pototskiy.apps.magemediation.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.magemediation.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

open class LongListAttributeStringWriter : AttributeWriterPlugin<LongListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(
        value: LongListType?,
        cell: Cell
    ) {
        (value as? LongListValue)?.let { list ->
            val listValue = ByteArrayOutputStream().use { stream ->
                stream.writer().use { writer ->
                    CSVFormat.RFC4180
                        .withQuote(quote)
                        .withDelimiter(delimiter)
                        .withRecordSeparator("")
                        .print(writer)
                        .printRecord(list.map { it.value.longToString(locale.createLocale()) })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
