package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.loader.SourceFileCollection
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class PrinterLineTest {
    @Suppress("PropertyName", "unused")
    class ImportCategory : Document() {
        var attr1: String = ""
        var entity_id: String = ""

        companion object : DocumentMetadata(ImportCategory::class)
    }

    private val helper = ConfigBuildHelper().also { helper ->
        helper.pushScope("printer")
        SourceFileCollection.Builder(helper).apply {
            file("mage-category") { path("no-path") }
        }.build()
    }

    @Test
    internal fun noPipelineAssemblerTest() {
        assertThatThrownBy {
            createPrinterLine()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Pipeline with the matched child must have assembler")
    }

    private fun createPrinterLine(): PrinterLine = PrinterLine.Builder(helper).apply {
        input {
            entity(ImportCategory::class)
        }
        output {
            file { file("mage-category"); sheet("default") }
            printHead = true
            outputFields {
                main("category") {
                    field("entity_id")
                }
            }
        }
        pipeline {
            classifier {
                it.match()
            }
            pipeline(Pipeline.CLASS.MATCHED) {

            }
            pipeline(Pipeline.CLASS.UNMATCHED) {

            }
        }
    }.build()
}
