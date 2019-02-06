package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.AttributeCollection
import net.pototskiy.apps.magemediation.api.config.data.EntityType

data class LoaderDataset(
    val name: String,
    val headersRow: Int,
    val rowsToSkip: Int,
    val maxAbsentDays: Int,
    val entityType: EntityType,
    val sources: SourceDataCollection,
    val fieldSets: List<FieldSet>
) {
    @ConfigDsl
    class Builder(
        private var name: String? = null,
        private var headersRow: Int? = null,
        private var rowsToSkip: Int = 0,
        private var maxAbsentDays: Int = 5,
        private var entityType: EntityType,
        private val sourceFiles: SourceFileCollection
    ) {
        private var sources: SourceDataCollection? = null
        private var fieldSets = mutableListOf<FieldSet>()

        @Suppress("unused")
        fun Builder.name(name: String): Builder = this.apply { this.name = name }

        @Suppress("unused")
        fun Builder.headersRow(row: Int): Builder = this.apply { this.headersRow = row }

        @Suppress("unused")
        fun Builder.rowsToSkip(rows: Int): Builder = this.apply { this.rowsToSkip = rows }

        @Suppress("unused")
        fun Builder.maxAbsentDays(days: Int): Builder = this.apply { this.maxAbsentDays = days }

        @Suppress("unused")
        fun Builder.sources(block: SourceDataCollection.Builder.() -> Unit): Builder =
            this.apply { this.sources = SourceDataCollection.Builder().apply(block).build() }

        @Suppress("unused")
        fun Builder.oneProductEntity(): Builder =
            this.apply { this.entityType = EntityType.OnecProduct }

        @Suppress("unused")
        fun Builder.oneGroupEntity(): Builder = this.apply { this.entityType = EntityType.OnecGroup }

        @Suppress("unused")
        fun Builder.oneGroupRelationEntity(): Builder =
            this.apply { this.entityType = EntityType.OnecGroupRelation }

        @Suppress("unused")
        fun Builder.mageProductEntity(): Builder =
            this.apply { this.entityType = EntityType.MageProduct }

        @Suppress("unused")
        fun Builder.mageCategoryEntity(): Builder =
            this.apply { this.entityType = EntityType.MageCategory }

        @Suppress("unused")
        fun Builder.magePriceEntity(): Builder = this.apply { this.entityType = EntityType.MagePrice }

        @Suppress("unused")
        fun Builder.mageInventoryEntity(): Builder =
            this.apply { this.entityType = EntityType.MageInventory }

        @Suppress("unused")
        fun Builder.mageCustomerGroupEntity(): Builder =
            this.apply { this.entityType = EntityType.MageCustomerGroup }

        @Suppress("unused")
        fun Builder.main(name: String, block: FieldSet.Builder.() -> Unit): Builder =
            this.apply {
                fieldSets.add(
                    FieldSet.Builder(name, true, AttributeCollection(emptyList()), false)
                        .apply(block).build()
                )
            }

        fun Builder.secondary(name: String, block: FieldSet.Builder.() -> Unit): Builder =
            this.apply {
                fieldSets.add(
                    FieldSet.Builder(
                        name,
                        false,
                        AttributeCollection(emptyList()),
                        false
                    ).apply(block).build()
                )
            }

        fun build(): LoaderDataset {
            val name = this.name ?: throw ConfigException("Dataset must have name")
            val headersRow = this.headersRow ?: UNDEFINED_COLUMN
            val rowsToSkip = this.rowsToSkip
            val target = this.entityType
            val sources = this.sources ?: throw ConfigException("Dataset<$name> has no data source")
            validateFieldColumnDefinition()
            return LoaderDataset(
                name,
                headersRow,
                rowsToSkip,
                maxAbsentDays,
                target,
                sources,
                fieldSets
            )
        }

        private fun validateFieldColumnDefinition() {
            var fields = fieldSets
                .map { it.fields.toList() }
                .flatten()
                .toMap()
            fields = fields
                .filterNot { it.key.isNested }
                .filterNot { it.value.isSynthetic }
            if (this.headersRow == null && fields.any { it.key.column == UNDEFINED_COLUMN }) {
                throw ConfigException(
                    "Dataset has no headers row but " +
                            "fields<${fields.filter { it.key.column == UNDEFINED_COLUMN }.map { it.value.name }
                                .joinToString(", ")}> " +
                            "has no column defined"
                )
            }
        }
    }
}
