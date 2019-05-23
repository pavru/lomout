package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.loader.SourceFileCollection
import net.pototskiy.apps.lomout.api.config.mediator.Pipeline
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.database.EntityStatus
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.StringType
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class PrinterLineTest {
    private val typeManager = EntityTypeManager().also { manager ->
        manager.createEntityType("import-category", emptyList(), false).also { entity ->
            manager.initialAttributeSetup(
                entity, AttributeCollection(
                    listOf(
                        manager.createAttribute("attr1", StringType::class)
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
                filter {
                    with(DbEntityTable) {
                        it[currentStatus] eq EntityStatus.UPDATED
                    }
                }
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
