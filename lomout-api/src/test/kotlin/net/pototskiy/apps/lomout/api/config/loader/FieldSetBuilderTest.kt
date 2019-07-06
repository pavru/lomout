package net.pototskiy.apps.lomout.api.config.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.Key
import net.pototskiy.apps.lomout.api.document.NonAttribute
import net.pototskiy.apps.lomout.api.document.toAttribute
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class FieldSetBuilderTest {
    class TestEntity : Document() {
        @Suppress("unused")
        var f1: String = ""
        @NonAttribute
        var nonAttr: String = ""

        companion object : DocumentMetadata(TestEntity::class)
    }

    private val helper = ConfigBuildHelper()
    private val entity = TestEntity::class

    @Test
    internal fun noFieldDefinedTest() {
        val fs = FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = true
        ).apply {
        }
        Assertions.assertThatThrownBy { fs.build() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("At least one field must be defined for field set.")
    }

    @Test
    internal fun uniqueFieldNameTest() {
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                mainSet = true,
                withSourceHeaders = false,
                sources = null,
                headerRow = null,
                toAttribute = true
            ).apply {
                field("f1")
                field("f1")
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Field 'f1' is already defined.")
    }

    @Test
    internal fun uniqueFieldColumnTest() {
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                mainSet = true,
                withSourceHeaders = false,
                sources = null,
                headerRow = null,
                toAttribute = true
            ).apply {
                field("f1") { column(0) }
                field("f2") { column(0) }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining(
                "Entity type " +
                        "'net.pototskiy.apps.lomout.api.config.loader.FieldSetBuilderTest.TestEntity' has no attribute for the 'f2'"
            )
    }

    @Test
    internal fun fromToAttributeTest() {
        var set = FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = true
        ).apply {
            field("f1") to TestEntity::f1.toAttribute()
        }.build()
        assertThat(set.fieldToAttr[set.fields.find { it.name == "f1" }]).isEqualTo(TestEntity::f1.toAttribute())
        set = FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = false
        ).apply {
            field("f1") from TestEntity::f1.toAttribute()
        }.build()
        assertThat(set.fieldToAttr[set.fields.find { it.name == "f1" }]).isEqualTo(TestEntity::f1.toAttribute())
        assertThatThrownBy { FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = true
        ).apply {
            field("f1") from TestEntity::f1.toAttribute()
        }.build() }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Use 'to' mapping operator.")
        assertThatThrownBy { FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = false
        ).apply {
            field("f1") to TestEntity::f1.toAttribute()
        }.build() }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Use 'from' mapping operator.")
    }

    @Test
    internal fun fromToPropertyTest() {
        var set = FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = true
        ).apply {
            field("f1") to TestEntity::f1
        }.build()
        assertThat(set.fieldToAttr[set.fields.find { it.name == "f1" }]).isEqualTo(TestEntity::f1.toAttribute())
        set = FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = false
        ).apply {
            field("f1") from TestEntity::f1
        }.build()
        assertThat(set.fieldToAttr[set.fields.find { it.name == "f1" }]).isEqualTo(TestEntity::f1.toAttribute())
        assertThatThrownBy { FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = true
        ).apply {
            field("f1") from TestEntity::f1
        }.build() }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Use 'to' mapping operator.")
        assertThatThrownBy { FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = true
        ).apply {
            field("f1") to TestEntity::nonAttr
        }.build() }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Attribute 'nonAttr' is not defined.")
        assertThatThrownBy { FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = false
        ).apply {
            field("f1") to TestEntity::f1
        }.build() }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Use 'from' mapping operator.")
        assertThatThrownBy { FieldSet.Builder(
            helper,
            entity,
            "test",
            mainSet = true,
            withSourceHeaders = false,
            sources = null,
            headerRow = null,
            toAttribute = false
        ).apply {
            field("f1") from TestEntity::nonAttr
        }.build() }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Attribute 'nonAttr' is not defined.")
    }

    @Test
    internal fun assignUndefinedAttributeTest() {
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                mainSet = true,
                withSourceHeaders = false,
                sources = null,
                headerRow = null,
                toAttribute = true
            ).apply {
                field("f1") to attribute("f10")
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Attribute 'f10' is not defined.")
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                mainSet = true,
                withSourceHeaders = false,
                sources = null,
                headerRow = null,
                toAttribute = false
            ).apply {
                field("f1") from attribute("f10")
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Attribute 'f10' is not defined.")
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                mainSet = true,
                withSourceHeaders = false,
                sources = null,
                headerRow = null,
                toAttribute = true
            ).apply {
                field("f1") from attribute("f10")
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Use 'to' mapping operator.")
        assertThatThrownBy {
            FieldSet.Builder(
                helper,
                entity,
                "test",
                mainSet = true,
                withSourceHeaders = false,
                sources = null,
                headerRow = null,
                toAttribute = false
            ).apply {
                field("f1") to attribute("f10")
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Use 'from' mapping operator.")
    }

    @Test
    internal fun createFieldSetWithoutMainTest() {
        assertThatThrownBy { createConfWithoutMainSet() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Field set collection must contain main set")
    }

    open class EntityType : Document() {
        @Suppress("unused")
        @Key
        var key: Long = 0L
        var data: String = ""

        companion object : DocumentMetadata(EntityType::class)
    }

    class OutputType : EntityType() {
        companion object : DocumentMetadata(OutputType::class)
    }

    private fun createConfWithoutMainSet(): Config {
        return Config.Builder(helper).apply {
            database {
                server {
                    host("localhost")
                    port(3306)
                    user("root")
                    if (System.getenv("TRAVIS_BUILD_DIR") == null) {
                        password("root")
                    } else {
                        password("")
                    }
                }
            }
            loader {
                files {
                    val testDataDir = System.getenv("TEST_DATA_DIR")
                    file("test-data") { path("$testDataDir/entity-loader-add-test.csv") }
                }
                loadEntity(EntityType::class) {
                    fromSources { source { file("test-data"); sheet("default"); stopOnEmptyRow() } }
                    rowsToSkip(1)
                    keepAbsentForDays(1)
                    sourceFields {
                        extra("entity") {
                            field("key") { column(0) }
                            field("data") { column(1) }
                        }
                    }
                }
            }
            mediator {
                productionLine {
                    output(OutputType::class)
                    input {
                        entity(EntityType::class)
                    }
                    pipeline {
                        assembler { _, _ -> emptyMap() }
                    }
                }
            }
        }.build()
    }

}
