package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.reader
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.nested.NestedAttributeWorkbook
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import kotlin.reflect.full.createInstance

/**
 * Default reader for [Document] attribute
 *
 * @property quote Char? The name-value pair quote, optional. This is parameter
 * @property delimiter Char The delimiter between pairs, default:','. This is parameter
 * @property valueQuote Char? The value quote, optional. This is parameter
 * @property valueDelimiter Char The delimiter between name and value, default:'='. This is parameter
 */
open class DocumentAttributeReader : AttributeReader<Document?>() {
    var quote: Char? = null
    var delimiter: Char = ','
    var valueQuote: Char? = '"'
    var valueDelimiter: Char = '='

    override fun read(attribute: DocumentMetadata.Attribute, input: Cell): Document? {
        return when (input.cellType) {
            CellType.STRING -> {
                if (input.stringValue.isBlank()) return null
                val attrs = NestedAttributeWorkbook(
                    quote,
                    delimiter,
                    valueQuote,
                    valueDelimiter,
                    attribute.name
                )
                attrs.string = input.stringValue
                val names = attrs[0][0]!!
                val values = attrs[0][1]!!
                val doc = try {
                    attribute.klass.createInstance() as Document
                } catch (e: IllegalArgumentException) {
                    throw AppConfigException(
                        badPlace(attribute) + input, message("message.error.data.document.cannot_create")
                    )
                }
                val metaData = doc.documentMetadata
                names.forEachIndexed { c, cell ->
                    if (cell != null) {
                        val attrName = cell.stringValue
                        metaData.attributes[attrName]?.let {
                            doc.setAttribute(cell.stringValue, it.reader.read(it, values.getOrEmptyCell(c)))
                        }
                    }
                }
                doc
            }
            CellType.BLANK -> null
            else -> throw AppDataException(
                badPlace(input) + attribute,
                message("message.error.data.document.cannot_read")
            )
        }
    }
}
