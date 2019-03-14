package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.database.EntityStatus
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.LongValue
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.StringValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("MagicNumber")
internal class MediatorConfigurationBasicTest {

    @Test
    internal fun checkConfTest() {
        val conf = createConf()
        assertThat(conf).isNotNull.isInstanceOf(MediatorConfiguration::class.java)
    }

    private fun createConf(): MediatorConfiguration {
        val typeManager = EntityTypeManager().also { typeManager ->
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
        }
        val helper = ConfigBuildHelper(typeManager)
        return MediatorConfiguration.Builder(helper).apply {
            crossProductionLine {
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
                            reader { _, _ -> StringValue("extended value") }
                        }
                    }
                    entity("input-entity-2") {
                        filter {
                            with(DbEntityTable) {
                                it[currentStatus] eq EntityStatus.UPDATED
                            }
                        }
                        extAttribute<LongType>("extAttr2", "inAttr2") {
                            reader { _, _ -> LongValue(33L) }
                        }
                    }
                }
                pipeline {
                    classifier {
                        Pipeline.CLASS.MATCHED
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler { _, _ -> emptyMap() }
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier { Pipeline.CLASS.MATCHED }
                        assembler { _, _ -> emptyMap() }
                    }
                }
            }
        }.build()
    }
}
