package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class ParentEntityTypeTest {
    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)
    @Suppress("UNCHECKED_CAST")
    private val attr1 = typeManager.createAttribute(
        "attr1", STRING::class,
        builder = null,
        reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
        writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
    )
    @Suppress("UNCHECKED_CAST")
    private val attr2 = typeManager.createAttribute(
        "attr2", STRING::class,
        builder = null,
        reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
        writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
    )
    @Suppress("UNCHECKED_CAST")
    private val attr3 = typeManager.createAttribute(
        "attr3", STRING::class,
        builder = null,
        reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
        writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
    )

    @BeforeEach
    internal fun setUp() {
        typeManager.createEntityType("entity", false).also {
            typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2, attr3)))
        }
    }

    @Test
    internal fun buildInheritanceWithIncludeTest() {
        val parent = ParentEntityType.Builder(helper, typeManager["entity"]).apply {
            include("attr1", "attr3")
        }.build()
        assertThat(parent.parent).isEqualTo(typeManager["entity"])
        assertThat(parent.exclude).isNull()
        assertThat(parent.include)
            .hasSize(2)
            .isEqualTo(AttributeCollection(listOf(attr1, attr3)))
        assertThatThrownBy {
            ParentEntityType.Builder(helper, typeManager["entity"]).apply {
                include("attr4")
            }
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Entity type 'entity' has no attributes 'attr4'.")
    }

    @Test
    internal fun buildInheritanceWithExcludeTest() {
        val parent = ParentEntityType.Builder(helper, typeManager["entity"]).apply {
            exclude("attr1", "attr3")
        }.build()
        assertThat(parent.parent).isEqualTo(typeManager["entity"])
        assertThat(parent.include).isNull()
        assertThat(parent.exclude)
            .hasSize(2)
            .isEqualTo(AttributeCollection(listOf(attr1, attr3)))
        assertThatThrownBy {
            ParentEntityType.Builder(helper, typeManager["entity"]).apply {
                exclude("attr4")
            }
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Entity type 'entity' has no attributes 'attr4'")
    }
}
