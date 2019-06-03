package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class ParentEntityTypeTest {
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)
    private val attr1 = typeManager.createAttribute("attr1", StringType::class)
    private val attr2 = typeManager.createAttribute("attr2", StringType::class)
    private val attr3 = typeManager.createAttribute("attr3", StringType::class)

    @BeforeEach
    internal fun setUp() {
        typeManager.createEntityType("entity", emptyList(), false).also {
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(
                        attr1, attr2, attr3
                    )
                )
            )
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
