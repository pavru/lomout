package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.entity.EntityStatus
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class MediatorConfigurationBuilderTest {
    private val typeManager = EntityTypeManagerImpl().also { typeManager ->
        typeManager.createEntityType("input-entity-1", false).also {
            @Suppress("UNCHECKED_CAST")
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(
                        typeManager.createAttribute(
                            "inAttr1",
                            STRING::class,
                            builder = null,
                            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
                            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
                        )
                    )
                )
            )
        }
        typeManager.createEntityType("input-entity-2", false).also {
            @Suppress("UNCHECKED_CAST")
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(
                        typeManager.createAttribute(
                            "inAttr2",
                            STRING::class,
                            builder = null,
                            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
                            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
                        )
                    )
                )
            )
        }
        typeManager.createEntityType("import-output-9", false).also {
            @Suppress("UNCHECKED_CAST")
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(
                        typeManager.createAttribute(
                            "outAttr99",
                            STRING::class,
                            builder = null,
                            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
                            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
                        )
                    )
                )
            )
        }
    }
    private val helper = ConfigBuildHelper(typeManager)

    @Test
    internal fun checkConfTest() {
        val conf = createConf()
        assertThat(conf).isNotNull.isInstanceOf(MediatorConfiguration::class.java)
        assertThat(conf.lines).hasSize(1)
        val line = conf.lines.first()
        assertThat(line).isInstanceOf(ProductionLine::class.java)
        assertThat(line.inputEntities).hasSize(2)
        assertThat(line.inputEntities.map { it.entity.name }).containsExactlyElementsOf(
            listOf(
                "input-entity-1", "input-entity-2"
            )
        )
        val inputEntity = line.inputEntities.first()
        assertThat(inputEntity).isNotNull
        assertThat(inputEntity.entity.name).isEqualTo("input-entity-1")
        assertThat(inputEntity.statuses).containsExactlyElementsOf(
            listOf(
                EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED
            )
        )
        assertThat(inputEntity.extAttributes).hasSize(1)
        assertThat(inputEntity.extAttributes.first().name).isEqualTo("extAttr1")
        val outputEntity = line.outputEntity
        assertThat(outputEntity).isNotNull
        assertThat(outputEntity.name).isEqualTo("import-output-1")
        assertThat(outputEntity.attributes).hasSize(2)
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
    internal fun checkInputEntityDefinedTest() {
        assertThatThrownBy { createConfInputNotExists() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Entity has not been defined yet.")
    }

    @Test
    internal fun predefinedOutputEntityTest() {
        val conf = createConfOutputDefined()
        assertThat(conf).isNotNull
        assertThat(conf.lines[0].outputEntity.name).isEqualTo("import-output-9")
    }

    @Test
    internal fun noExtAttributeBuilderTest() {
        assertThatThrownBy { createConfNoExtAttributeBuilder() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Extension attribute must have builder")
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
                output("import-output-1") {
                    attribute<STRING>("outAttr1")
                    attribute<LONG>("outAttr2")
                }
                input {
                    entity("input-entity-1") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                        extAttribute<STRING>("extAttr1") {
                            builder { STRING("extended value from the builder") }
                        }
                    }
                    entity("input-entity-2") {
                        statuses(EntityStatus.UPDATED)
                        extAttribute<LONG>("extAttr2") {
                            builder { LONG(32L) }
                        }
                    }
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

    private fun createConfNoExtAttributeBuilder(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output("import-output-1") {
                    attribute<STRING>("outAttr1")
                    attribute<LONG>("outAttr2")
                }
                input {
                    entity("input-entity-1") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                        extAttribute<STRING>("extAttr1") {
                            reader { _, _ -> STRING("extended value") }
                        }
                    }
                    entity("input-entity-2") {
                        statuses(EntityStatus.UPDATED)
                        extAttribute<LONG>("extAttr2") {
                            builder { LONG(32L) }
                        }
                    }
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

    private fun createConfInputNotExists(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output("import-output-1") {
                    attribute<STRING>("outAttr1")
                    attribute<LONG>("outAttr2")
                }
                input {
                    entity("input-entity-3") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                        extAttribute<STRING>("extAttr1") {
                            builder { STRING("extended value from the builder") }
                        }
                    }
                    entity("input-entity-2") {
                        statuses(EntityStatus.UPDATED)
                        extAttribute<LONG>("extAttr2") {
                            builder { LONG(32L) }
                        }
                    }
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
                output("import-output-1") {
                    attribute<STRING>("outAttr1")
                    attribute<LONG>("outAttr2")
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

    private fun createConfOutputDefined(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output("import-output-9")
                input {
                    entity("input-entity-1") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                        extAttribute<STRING>("extAttr1") {
                            builder { STRING("extended value from the builder") }
                        }
                    }
                    entity("input-entity-2") {
                        statuses(EntityStatus.UPDATED)
                        extAttribute<LONG>("extAttr2") {
                            builder { LONG(32L) }
                        }
                    }
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

    private fun createConfNoPipeline(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output("import-output-1") {
                    attribute<STRING>("outAttr1")
                    attribute<LONG>("outAttr2")
                }
                input {
                    entity("input-entity-1") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                        extAttribute<STRING>("extAttr1") {
                            builder { STRING("extended value from the builder") }
                        }
                    }
                    entity("input-entity-2") {
                        statuses(EntityStatus.UPDATED)
                        extAttribute<LONG>("extAttr2") {
                            builder { LONG(32L) }
                        }
                    }
                }
            }
        }.build()
    }

    private fun createConfNoAssembler(): MediatorConfiguration {
        return MediatorConfiguration.Builder(helper).apply {
            productionLine {
                output("import-output-1") {
                    attribute<STRING>("outAttr1")
                    attribute<LONG>("outAttr2")
                }
                input {
                    entity("input-entity-1") {
                        statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                        extAttribute<STRING>("extAttr1") {
                            builder { STRING("extended value from the builder") }
                        }
                    }
                    entity("input-entity-2") {
                        statuses(EntityStatus.UPDATED)
                        extAttribute<LONG>("extAttr2") {
                            builder { LONG(32L) }
                        }
                    }
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
