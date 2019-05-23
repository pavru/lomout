package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.database.EntityStatus
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.StringType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
@Suppress("TooManyFunctions")
internal class PrinterConfigurationBuilderTest {
    private val typeManager = EntityTypeManager().also { manager ->
        manager.createEntityType("entity1", emptyList(), false).also { type ->
            manager.initialAttributeSetup(
                type, AttributeCollection(
                    listOf(
                        manager.createAttribute("attr1", StringType::class),
                        manager.createAttribute("attr2", StringType::class),
                        manager.createAttribute("attr3", StringType::class)
                    )
                )
            )
        }
        manager.createEntityType("entity2", emptyList(), false).also { type ->
            manager.initialAttributeSetup(
                type, AttributeCollection(
                    listOf(
                        manager.createAttribute("attr1", StringType::class),
                        manager.createAttribute("attr2", StringType::class),
                        manager.createAttribute("attr3", StringType::class)
                    )
                )
            )
        }
    }

    @Test
    internal fun correctConfigurationTest() {
        val config = createCorrectConfiguration()
        assertThat(config).isNotNull
        assertThat(config.files).hasSize(2)
        assertThat(config.lines).hasSize(2)
        with(config.lines[0]) {
            assertThat(inputEntities).hasSize(1)
            assertThat(inputEntities[0].extAttrMaps).hasSize(0)
            assertThat(outputFieldSets).isNotNull
            assertThat(outputFieldSets.file.file.id).isEqualTo("id1")
            assertThat(outputFieldSets.printHead).isTrue()
            assertThat(outputFieldSets.fieldSets).hasSize(2)
            assertThat(pipeline).isNotNull
            assertThat(config.lines.contains(this)).isTrue()
            assertThat(config.lines.indexOf(this)).isEqualTo(0)
            assertThat(config.lines.lastIndexOf(this)).isEqualTo(0)
            Unit
        }
    }

    @Test
    internal fun tooManyInputsTest() {
        assertThatThrownBy {
            createConfigurationTooManyInput()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("One and only one input entity is allowed for printer line")
    }

    @Test
    internal fun inputHasExtAttrTest() {
        assertThatThrownBy {
            createConfigurationWithExtAttr()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Input entity of printer line cannot have extended attributes")
    }

    @Test
    internal fun noInputsTest() {
        assertThatThrownBy {
            createConfigurationNoInputs()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Input entities must be defined")
    }

    @Test
    internal fun inputOutputDisorderTest() {
        assertThatThrownBy {
            createConfigurationInputOutputDisorder()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Input must be defined before output")
    }

    @Test
    internal fun noOutputsTest() {
        assertThatThrownBy {
            createConfigurationNoOutputs()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Output fields must be defined")
    }

    @Test
    internal fun noPipelineTest() {
        assertThatThrownBy {
            createConfigurationNoPipeline()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Pipeline must be defined")
    }

    @Test
    internal fun noOutputFileTest() {
        assertThatThrownBy {
            createConfigurationNoOutputFile()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Output file must be defined")
    }

    @Test
    internal fun noFieldSetTest() {
        assertThatThrownBy {
            createConfigurationNoFieldSet()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Field sets must be defined")
    }

    @Test
    internal fun sheetWithRegexTest() {
        assertThatThrownBy {
            createConfigurationWithSheetRegex()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Sheet name, not regex must be used in output")
    }

    private fun createCorrectConfiguration(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id1"); sheet("test") }
                    printHead = true
                    outputFields {
                        main("main") {
                            field("attr1") { column(0) }
                            field("attr2") { column(1) }
                        }
                        extra("extra") {
                            field("attr3") { column(0) }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()

    private fun createConfigurationTooManyInput(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                    entity("entity2")
                }
                output {
                    file { file("id1"); sheet("test") }
                    printHead = true
                    outputFields {
                        main("main") {
                            field("attr1") { column(0) }
                            field("attr2") { column(1) }
                        }
                        extra("extra") {
                            field("attr3") { column(0) }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()

    private fun createConfigurationWithExtAttr(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                        extAttribute<StringType>("extAttr1", "attr1")
                    }
                }
                output {
                    file { file("id1"); sheet("test") }
                    printHead = true
                    outputFields {
                        main("main") {
                            field("attr1") { column(0) }
                            field("attr2") { column(1) }
                        }
                        extra("extra") {
                            field("attr3") { column(0) }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()

    private fun createConfigurationInputOutputDisorder(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                output {
                    file { file("id1"); sheet("test") }
                    printHead = true
                    outputFields {
                        main("main") {
                            field("attr1") { column(0) }
                            field("attr2") { column(1) }
                        }
                        extra("extra") {
                            field("attr3") { column(0) }
                        }
                    }
                }
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()

    private fun createConfigurationNoInputs(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()

    private fun createConfigurationNoOutputs(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()

    private fun createConfigurationNoPipeline(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id1"); sheet("test") }
                    printHead = true
                    outputFields {
                        main("main") {
                            field("attr1") { column(0) }
                            field("attr2") { column(1) }
                        }
                        extra("extra") {
                            field("attr3") { column(0) }
                        }
                    }
                }
            }
        }.build()

    private fun createConfigurationNoOutputFile(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    printHead = true
                    outputFields {
                        main("main") {
                            field("attr1") { column(0) }
                            field("attr2") { column(1) }
                        }
                        extra("extra") {
                            field("attr3") { column(0) }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()

    private fun createConfigurationNoFieldSet(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id1"); sheet("test") }
                    printHead = true
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()

    private fun createConfigurationWithSheetRegex(): PrinterConfiguration =
        PrinterConfiguration.Builder(ConfigBuildHelper(typeManager)).apply {
            files {
                file("id1") { path("file1") }
                file("id2") { path("file2") }
            }
            printerLine {
                input {
                    entity("entity1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id1"); sheet(Regex(".*")) }
                    printHead = true
                    outputFields {
                        main("main") {
                            field("attr1") { column(0) }
                            field("attr2") { column(1) }
                        }
                        extra("extra") {
                            field("attr3") { column(0) }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
            printerLine {
                input {
                    entity("entity2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                    }
                }
                output {
                    file { file("id2"); sheet("test") }
                    printHead = false
                    outputFields {
                        main("main") {
                            field("attr3")
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { _, _ -> emptyMap() }
                }
            }
        }.build()
}
