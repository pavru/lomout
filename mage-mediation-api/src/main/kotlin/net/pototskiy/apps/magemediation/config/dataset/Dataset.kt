package net.pototskiy.apps.magemediation.config.dataset

import net.pototskiy.apps.magemediation.LOG_NAME
import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.ConfigValidate
import net.pototskiy.apps.magemediation.config.DatasetTarget
import net.pototskiy.apps.magemediation.config.dataset.Field.Companion.UNDEFINED_COLUMN
import org.apache.log4j.Logger
import javax.xml.bind.annotation.*

@XmlType(name = "data-set")
@XmlAccessorType(XmlAccessType.FIELD)
class Dataset : ConfigValidate {
    @field:XmlElement(name = "source")
    var sources: List<DataSource> = mutableListOf()
    @field:XmlElementWrapper(name = "field-sets")
    @field:XmlElements(
        XmlElement(name = "main", type = MainFieldSet::class),
        XmlElement(name = "additional", type = AdditionalFieldSet::class)
    )
    var fieldSets: List<FieldSet> = mutableListOf()
    @field:XmlAttribute(required = true)
    var name: String = ""
    @field:XmlAttribute(name = "headers-row")
    var headersRow: Int = UNDEFINED_COLUMN
    @field:XmlAttribute(name = "rows-to-skip")
    var rowsToSkip: Int = 0
    @field:XmlAttribute(name = "max-absent-age")
    var maxAbsentDays: Int = 5
    @field:XmlAttribute(required = true)
    var target: DatasetTarget = DatasetTarget.MAGE_PRODUCT

    @Suppress("unused")
    @XmlTransient
    private val logger = Logger.getLogger(LOG_NAME)

    override fun validate(parent: Any?) {
        validateDuplicatedNames()
        validateNoHeadersColumn()
    }

    private fun validateNoHeadersColumn() {
        if (headersRow == UNDEFINED_COLUMN) {
            val noColumn = fieldSets
                .flatMap { it.fields }
                .filter { it.column == UNDEFINED_COLUMN && !it.nested}
                .map { it.name }
            if (noColumn.count() != 0) {
                throw ConfigException(
                    "Dataset<$name> has no headers row but fields<${noColumn.joinToString(
                        ", "
                    )} have no column definition>"
                )
            }
        }
    }

    private fun validateDuplicatedNames() {
        val fields = fieldSets.flatMap { it.fields }
            .groupBy { it.name }
            .filter { it.value.count() > 1 }
        if (fields.any { it.value.count() > 1 }) {
            throw ConfigException(
                "Dataset<$name> has fields<${fields.keys.joinToString(
                    ", "
                )}> with duplicated name"
            )
        }
    }
}
