package net.pototskiy.apps.lomout.api.entity.writer

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.entity.LongListType
import net.pototskiy.apps.lomout.api.entity.values.longToString
import net.pototskiy.apps.lomout.api.plugable.AttributeWriterPlugin
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import org.apache.commons.csv.CSVFormat
import java.io.ByteArrayOutputStream

/**
 * Default writer for [LongListType] attribute
 *
 * @property locale String The value locale, default system locale
 * @property quote Char? The value quote, optional
 * @property delimiter Char The list delimiter, default: ,
 */
open class LongListAttributeStringWriter : AttributeWriterPlugin<LongListType>() {
    var locale: String = DEFAULT_LOCALE_STR
    var quote: Char? = null
    var delimiter: Char = ','

    override fun write(
        value: LongListType?,
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
                        .printRecord(list.map { it.value.longToString(locale.createLocale()) })
                }
                stream.toString()
            }
            cell.setCellValue(listValue)
        }
    }
}
