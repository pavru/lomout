package net.pototskiy.apps.magemediation.dsl.config.loader.dataset

import net.pototskiy.apps.magemediation.api.UNDEFINED_COLUMN
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.DatasetTarget
import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.loader.dataset.DataSourceConfiguration
import net.pototskiy.apps.magemediation.api.config.loader.dataset.DatasetConfiguration
import net.pototskiy.apps.magemediation.api.config.loader.dataset.FieldSetConfiguration

@ConfigDsl
class DatasetConfigurationBuilder(
    private var name: String? = null,
    private var headersRow: Int? = null,
    private var rowsToSkip: Int = 0,
    private var maxAbsentDays: Int = 5,
    private var target: DatasetTarget? = null
) {
    private var sources: List<DataSourceConfiguration>? = null
    private var fieldSets: List<FieldSetConfiguration>? = null

    @Suppress("unused")
    fun DatasetConfigurationBuilder.name(name: String) {
        this.name = name
    }

    @Suppress("unused")
    fun DatasetConfigurationBuilder.headersRow(row: Int) {
        this.headersRow = row
    }

    @Suppress("unused")
    fun DatasetConfigurationBuilder.rowsToSkip(rows: Int) {
        this.rowsToSkip = rows
    }

    @Suppress("unused")
    fun DatasetConfigurationBuilder.maxAbsentDays(days: Int) {
        this.maxAbsentDays = days
    }

    @Suppress("unused")
    fun DatasetConfigurationBuilder.target(target: DatasetTarget) {
        this.target = target
    }

    @Suppress("unused")
    fun DatasetConfigurationBuilder.sources(block: DataSourceConfigurationsBuilder.() -> Unit) {
        sources = DataSourceConfigurationsBuilder().apply(block).build()
    }

    @Suppress("unused")
    fun DatasetConfigurationBuilder.fieldSets(block: FieldSetConfigurationsBuilder.() -> Unit) {
        fieldSets = FieldSetConfigurationsBuilder().apply(block).build()
    }

    fun build(): DatasetConfiguration {
        val name = this.name ?: throw ConfigException("Dataset must have name")
        val headersRow = this.headersRow ?: UNDEFINED_COLUMN
        val rowsToSkip = this.rowsToSkip
        val target = this.target ?: throw ConfigException("Dataset<$name> must have target")
        val sources = this.sources ?: listOf()
        val fieldSets = this.fieldSets ?: listOf()
        validateFieldColumnDefinition()
        return DatasetConfiguration(
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
        val fields = fieldSets!!.flatMap { it.fields }.filter { !it.nested }
        if (this.headersRow == null && fields.any { it.column == UNDEFINED_COLUMN }) {
            throw ConfigException(
                "Dataset has no headers row but " +
                        "fields<${fields.filter { it.column == UNDEFINED_COLUMN }.joinToString(
                            ", "
                        )}> " +
                        "has no column defined"
            )
        }
    }
}