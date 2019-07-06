package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.values.stringToDate
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import org.apache.commons.csv.CSVFormat
import java.time.LocalDate

/**
 * Default reader for **List&lt;LocalDate&gt;** attribute
 *
 * @property locale String The value locale. This is parameter
 * @property pattern String? The value pattern, optional (use locale). This is parameter
 * @property quote Char? The value quote, optional. This is parameter
 * @property delimiter Char The list delimiter, default:','. This is parameter
 */
open class DateListAttributeReader : AttributeReader<List<LocalDate>?>() {
    var locale: String = DEFAULT_LOCALE_STR
    var pattern: String? = null
    var quote: Char? = null
    var delimiter: Char = ','

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): List<LocalDate>? {
        return when (input.cellType) {
            CellType.STRING -> {
                input.stringValue.reader().use { reader ->
                    try {
                        CSVFormat.RFC4180
                            .withQuote(quote)
                            .withDelimiter(delimiter)
                            .withRecordSeparator("")
                            .parse(reader)
                            .records
                            .map { it.toList() }.flatten()
                            .map { data ->
                                (pattern?.let { data.stringToDate(it) }
                                    ?: data.stringToDate(locale.createLocale()))
                            }
                    } catch (e: AppDataException) {
                        throw AppDataException(badPlace(attribute) + input, e.message, e)
                    }
                }
            }
            CellType.BLANK -> null
            else -> throw AppDataException(
                badPlace(input) + attribute,
                "Cannot read Date list from the cell."
            )
        }
    }
}
