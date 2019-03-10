package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.StringType
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class FieldSetBuilderTest {
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)
    private val entity = typeManager.createEntityType("test", emptyList(), true)

    @Test
    internal fun noFieldDefinedTest() {
        val fs = FieldSet.Builder(
            helper,
            entity,
            "test",
            true,
            false,
            null,
            null
        ).apply {
        }
        Assertions.assertThatThrownBy { fs.build() }.isInstanceOf(ConfigException::class.java)
    }

    @Test
    internal fun uniqueFieldNameTest() {
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                true,
                false,
                null,
                null
            ).apply {
                field("f1")
                field("f1")
            }.build()
        }.isInstanceOf(ConfigException::class.java)
    }

    @Test
    internal fun uniqueFieldColumnTest() {
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                true,
                false,
                null,
                null
            ).apply {
                field("f1") { column(0) }
                field("f2") { column(0) }
            }.build()
        }.isInstanceOf(ConfigException::class.java)
    }

    @Test
    internal fun assignUndefinedAttributeTest() {
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                true,
                false,
                null,
                null
            ).apply {
                field("f1") to attribute("f1")
            }.build()
        }.isInstanceOf(ConfigException::class.java)
    }

    @Test
    internal fun assignAttributeWithNullNameTest() {
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                true,
                false,
                null,
                null
            ).apply {
                field("f1") to attribute<StringType>(null) {}
            }.build()
        }.isInstanceOf(ConfigException::class.java)
    }

    @Test
    internal fun assignAttributeWithDefinitionTest() {
        assertThat(
            FieldSet.Builder(
                helper,
                entity,
                "test",
                true,
                false,
                null,
                null
            ).apply {
                field("f1") to attribute<StringType>("f2") {}
            }.build()
        ).isNotNull
    }

    @Test
    internal fun wrongParentTest() {
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                true,
                false,
                null,
                null
            ).apply {
                field("f1")
                field("f2") { parent("f3") }
            }.build()
        }.isInstanceOf(ConfigException::class.java)
    }

    @Test
    internal fun cycleNestedParentTest() {
        assertThat(
            FieldSet.Builder(
                helper,
                entity,
                "test",
                true,
                false,
                null,
                null
            ).apply {
                field("f1") { } to attribute<AttributeListType>("f1") {}
                field("f2") { parent("f1") } to attribute<AttributeListType>("f2") {}
                field("f3") { parent("f2") } to attribute<AttributeListType>("f3") {}
            }.build()
        ).isNotNull
    }
}
