package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.UNDEFINED_ROW
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.EType

data class Load(
    val headersRow: Int,
    val rowsToSkip: Int,
    val maxAbsentDays: Int,
    val entity: EType,
    val sources: SourceDataCollection,
    val fieldSets: FieldSetCollection
) {
    @ConfigDsl
    class Builder(private val eType: EType) {
        private var headersRow: Int = UNDEFINED_ROW
        private var rowsToSkip: Int = 0
        private var maxAbsentDays: Int = defaultAbsentDays
        private var sources: SourceDataCollection? = null
        private var fieldSets: FieldSetCollection? = null

        fun headersRow(row: Int) {
            headersRow = row
        }

        fun rowsToSkip(rows: Int) {
            this.rowsToSkip = rows
        }

        fun keepAbsentForDays(days: Int) {
            this.maxAbsentDays = days
        }

        fun fromSources(block: SourceDataCollection.Builder.() -> Unit) {
            this.sources = SourceDataCollection.Builder().apply(block).build()
        }

        fun sourceFields(block: FieldSetCollection.Builder.() -> Unit) {
            fieldSets = FieldSetCollection.Builder(
                eType,
                headersRow != UNDEFINED_COLUMN,
                sources,
                headersRow
            ).apply(block).build()
        }

        fun build(): Load {
            refineEntityAttributes()
            val headersRow = this.headersRow
            val rowsToSkip = this.rowsToSkip
            val sources =
                this.sources
                    ?: throw ConfigException("Source files are not defined for entity type<${eType.type}> loading")
            validateFieldColumnDefinition()
            return Load(
                headersRow,
                rowsToSkip,
                maxAbsentDays,
                eType,
                sources,
                fieldSets
                    ?: throw ConfigException("Field set is not defined for entity type<${eType.type}> loading")
            )
        }

        private fun refineEntityAttributes() {
            val autoAttrs = fieldSets
                ?.map { it.fieldToAttr.attributes }
                ?.flatten()
                ?.filter { it.auto } ?: emptyList()
            autoAttrs.forEach { eType.addAttribute(it) }
        }

        private fun validateFieldColumnDefinition() {
            var fields =
                (fieldSets
                    ?: throw ConfigException("Field set is not defined for entity type<${eType.type}> loading"))
                    .map { it.fieldToAttr.toList() }
                    .flatten()
                    .toMap()
            fields = fields
                .filterNot { it.key.isNested }
                .filterNot { it.value.isSynthetic }
            if (this.headersRow == UNDEFINED_ROW && fields.any { it.key.column == UNDEFINED_COLUMN }) {
                throw ConfigException(
                    "Dataset has no headers row but " +
                            "fields<${fields.filter { it.key.column == UNDEFINED_COLUMN }.map { it.value.name }
                                .joinToString(", ")}> " +
                            "has no column defined"
                )
            }
        }

        companion object {
            const val defaultAbsentDays = 5
        }
    }
}
