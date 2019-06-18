package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.entity.type.STRING
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class EntityTypeTest {
    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)

    @Test
    fun inheritAbsentTypeTest() {
        val builder = EntityType.Builder(helper, "test1", false)
        assertThat(builder.entityType).isEqualTo("test1")
        assertThatThrownBy {
            builder.inheritFrom("test2")
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Entity type 'test2' is not defined.")
    }

    @Test
    internal fun createAttributeTest() {
        val builder = EntityType.Builder(helper, "test1", false)
        assertThat(builder.attributes).hasSize(0)
        builder.attribute<STRING>("attr1")
        assertThat(builder.attributes).hasSize(1)
    }
}
