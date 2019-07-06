package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.documentMetadata
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class MediatorConfigurationBuilderTest {
    @Suppress("unused")
    class InputEntity1 : Document() {
        var inAttr1: String = ""
        @get:BsonIgnore
        val extAttr1: String
            get() = "extended value from the builder"

        companion object : DocumentMetadata(InputEntity1::class)
    }

    @Suppress("unused")
    class InputEntity2 : Document() {
        var inAttr2: String = ""
        @get:BsonIgnore
        val extAttr2: Long
            get() = 32L

        companion object : DocumentMetadata(InputEntity2::class)
    }

    @Suppress("unused")
    class InputEntity3 : Document() {
        var inAttr: String = ""
    }

    @Suppress("unused")
    class ImportOutput1 : Document() {
        var outAttr1: String = ""
        var outAttr2: Long = 0L

        companion object : DocumentMetadata(ImportOutput1::class)
    }

    private val helper = ConfigBuildHelper()

    @Test
    internal fun checkConfTest() {
        val conf = createConf()
        assertThat(conf).isNotNull.isInstanceOf(MediatorConfiguration::class.java)
        assertThat(conf.lines).hasSize(1)
        val line = conf.lines.first()
        assertThat(line).isInstanceOf(ProductionLine::class.java)
        assertThat(line.inputEntities).hasSize(2)
        assertThat(line.inputEntities.map { it.entity.simpleName }).containsExactlyElementsOf(
            listOf("InputEntity1", "InputEntity2")
        )
        val inputEntity = line.inputEntities.first()
        assertThat(inputEntity).isNotNull
        assertThat(inputEntity.entity.simpleName).isEqualTo("InputEntity1")
        assertThat(inputEntity.includeDeleted).isEqualTo(false)
        val outputEntity = line.outputEntity
        assertThat(outputEntity).isNotNull
        assertThat(outputEntity.simpleName).isEqualTo("ImportOutput1")
        assertThat(outputEntity.documentMetadata.attributes.values).hasSize(2)
        val rootPipeline = line.pipeline
        assertThat(rootPipeline.classifier).isNotNull
        assertThat(rootPipeline.pipelines).hasSize(2)
        assertThat(rootPipeline.assembler).isNull()
        assertThat(rootPipeline.dataClass)
            .containsExactlyElementsOf(listOf(Pipeline.CLASS.MATCHED, Pipeline.CLASS.UNMATCHED))
        assertThat(rootPipeline.pipelines[0].dataClass)
            .containsExactlyElementsOf(listOf(Pipeline.CLASS.MATCHED))
        assertThat(rootPipeline.pipelines[0].classifier).isNotNull
        assertThat(rootPipeline.pipelines[0].pipelines).isEmpty()
        assertThat(rootPipeline.pipelines[0].assembler).isNotNull
        assertThat(rootPipeline.pipelines[1].dataClass)
            .containsExactlyElementsOf(listOf(Pipeline.CLASS.UNMATCHED))
        assertThat(rootPipeline.pipelines[1].classifier).isNotNull
        assertThat(rootPipeline.pipelines[1].pipelines).isEmpty()
        assertThat(rootPipeline.pipelines[1].assembler).isNotNull
    }

    @Test
    internal fun checkEmptyInputErrorTest() {
        assertThatThrownBy { createConfEmptyInput() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("At least one input entity must be defined")
    }

    @Test
    internal fun noPipelineTest() {
        assertThatThrownBy { createConfNoPipeline() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Production line must have start pipeline")
    }

    @Test
    internal fun noAssemblerTest() {
        assertThatThrownBy { createConfNoAssembler() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Pipeline with the matched child must have assembler")
    }

    private fun createConf(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output(ImportOutput1::class)
                input {
                    entity(InputEntity1::class)
                    entity(InputEntity2::class) { includeDeleted() }
                }
                pipeline {
                    classifier {
                        it.match()
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler { _, _ -> emptyMap() }
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier { it.match() }
                        assembler { _, _ -> emptyMap() }
                    }
                }
            }
        }.build()
    }

    private fun createConfEmptyInput(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output(ImportOutput1::class)
                pipeline {
                    classifier {
                        it.match()
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler { _, _ -> emptyMap() }
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier { it.match() }
                        assembler { _, _ -> emptyMap() }
                    }
                }
            }
        }.build()
    }

    private fun createConfNoPipeline(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output(ImportOutput1::class)
                input {
                    entity(InputEntity1::class)
                    entity(InputEntity2::class) { includeDeleted() }
                }
            }
        }.build()
    }

    private fun createConfNoAssembler(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output(ImportOutput1::class)
                input {
                    entity(InputEntity1::class)
                    entity(InputEntity2::class) { includeDeleted() }
                }
                pipeline {
                    classifier {
                        it.match()
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler { _, _ -> emptyMap() }
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier { it.match() }
                        pipeline(Pipeline.CLASS.UNMATCHED) {}
                        assembler { _, _ -> emptyMap() }
                    }
                }
            }
        }.build()
    }
}
