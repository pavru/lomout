package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppEntityTypeException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.database.EntityStatus
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.StringType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class MediatorConfigurationBuilderTest {
    private val typeManager = EntityTypeManager().also { typeManager ->
        typeManager.createEntityType("input-entity-1", emptyList(), false).also {
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(
                        typeManager.createAttribute("inAttr1", StringType::class)
                    )
                )
            )
        }
        typeManager.createEntityType("input-entity-2", emptyList(), false).also {
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(
                        typeManager.createAttribute("inAttr2", StringType::class)
                    )
                )
            )
        }
        typeManager.createEntityType("import-output-9", emptyList(), false).also {
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(
                        typeManager.createAttribute("outAttr99", StringType::class)
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
        assertThat(inputEntity.entityExtension).isNotNull
        assertThat(inputEntity.entityExtension?.name).matches("input-entity-1${"\\$\\$"}ext${"\\$\\$"}.*")
        assertThat(inputEntity.filter).isNotNull
        assertThat(inputEntity.extAttrMaps).hasSize(1)
        assertThat(inputEntity.extAttrMaps.keys.first().name).isEqualTo("extAttr1")
        assertThat(inputEntity.extAttrMaps.values.first().name).isEqualTo("inAttr1")
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
            .isInstanceOf(AppEntityTypeException::class.java)
            .hasMessageContaining("Entity<input-entity-3> has not been defined yet")
    }

    @Test
    internal fun predefinedOutputEntityTest() {
        val conf = createConfOutputDefined()
        assertThat(conf).isNotNull
        assertThat(conf.lines[0].outputEntity.name).isEqualTo("import-output-9")
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
                    attribute<StringType>("outAttr1")
                    attribute<LongType>("outAttr2")
                }
                input {
                    entity("input-entity-1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] neq EntityStatus.REMOVED
                            }
                        }
                        extAttribute<StringType>("extAttr1", "inAttr1") {
                            reader { _, _ -> StringType("extended value") }
                        }
                    }
                    entity("input-entity-2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                        extAttribute<LongType>("extAttr2", "inAttr2") {
                            reader { _, _ -> LongType(33L) }
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
                    attribute<StringType>("outAttr1")
                    attribute<LongType>("outAttr2")
                }
                input {
                    entity("input-entity-3") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] neq EntityStatus.REMOVED
                            }
                        }
                        extAttribute<StringType>("extAttr1", "inAttr1") {
                            reader { _, _ -> StringType("extended value") }
                        }
                    }
                    entity("input-entity-2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                        extAttribute<LongType>("extAttr2", "inAttr2") {
                            reader { _, _ -> LongType(33L) }
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
                    attribute<StringType>("outAttr1")
                    attribute<LongType>("outAttr2")
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
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] neq EntityStatus.REMOVED
                            }
                        }
                        extAttribute<StringType>("extAttr1", "inAttr1") {
                            reader { _, _ -> StringType("extended value") }
                        }
                    }
                    entity("input-entity-2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                        extAttribute<LongType>("extAttr2", "inAttr2") {
                            reader { _, _ -> LongType(33L) }
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
                    attribute<StringType>("outAttr1")
                    attribute<LongType>("outAttr2")
                }
                input {
                    entity("input-entity-1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] neq EntityStatus.REMOVED
                            }
                        }
                        extAttribute<StringType>("extAttr1", "inAttr1") {
                            reader { _, _ -> StringType("extended value") }
                        }
                    }
                    entity("input-entity-2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                        extAttribute<LongType>("extAttr2", "inAttr2") {
                            reader { _, _ -> LongType(33L) }
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
                    attribute<StringType>("outAttr1")
                    attribute<LongType>("outAttr2")
                }
                input {
                    entity("input-entity-1") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] neq EntityStatus.REMOVED
                            }
                        }
                        extAttribute<StringType>("extAttr1", "inAttr1") {
                            reader { _, _ -> StringType("extended value") }
                        }
                    }
                    entity("input-entity-2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                        extAttribute<LongType>("extAttr2", "inAttr2") {
                            reader { _, _ -> LongType(33L) }
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
