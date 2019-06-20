package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.loader.SourceFileCollection
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.entity.EntityStatus
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class PrinterLineTest {
    private val typeManager = EntityTypeManagerImpl().also { manager ->
        manager.createEntityType("import-category", emptyList(), false).also { entity ->
            @Suppress("UNCHECKED_CAST")
            manager.initialAttributeSetup(
                entity, AttributeCollection(
                    listOf(
                        manager.createAttribute(
                            "attr1", STRING::class,
                            builder = null,
                            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
                            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
                        )
                    )
                )
            )
        }
    }
    private val helper = ConfigBuildHelper(typeManager).also { helper ->
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
            entity("import-category") {
                statuses(EntityStatus.UPDATED)
            }
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
