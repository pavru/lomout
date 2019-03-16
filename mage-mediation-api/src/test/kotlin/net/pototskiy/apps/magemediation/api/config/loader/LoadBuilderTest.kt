package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.AppConfigException
import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class LoadBuilderTest {
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)
    private val entity = typeManager.createEntityType("entity", emptyList(), true)

    @Test
    internal fun validateFieldColumnsTest() {
        assertThatThrownBy {
            Load.Builder(helper, entity).apply {
                sourceFields {
                    main("test") {
                        field("f1") { column(0) }
                        field("f2") { column(0) }
                        field("f3") { column(1) }
                        field("f4") { column(1) }
                    }
                }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Field columns<0, 1> are duplicated")
    }

    @Test
    internal fun fieldColumnsNotDefinedTest() {
        SourceFileCollection.Builder(helper).apply {
            file("file1") { path("test") }
        }.build()
        assertThatThrownBy {
            Load.Builder(helper, entity).apply {
                fromSources { source { file("file1"); sheet("test") } }
                sourceFields {
                    main("test") {
                        field("f1") { }
                        field("f2") { }
                        field("f3") { }
                        field("f4") { }
                    }
                }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Dataset has no headers row but fields<f1, f2, f3, f4> has no column defined")
    }

    @Test
    internal fun sourcesNotDefinedTest() {
        assertThatThrownBy {
            Load.Builder(helper, entity).apply {
                sourceFields {
                    main("test") {
                        field("f1") { column(1) }
                        field("f2") { column(2) }
                    }
                }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Source files are not defined for entity type<entity> loading")
    }

    @Test
    internal fun sourceFieldsNotDefinedTest() {
        val v = SourceFileCollection.Builder(helper).apply {
            file("file1") { path("test") }
        }.build()
        assertThat(v).isNotNull
        assertThatThrownBy {
            Load.Builder(helper, entity).apply {
                fromSources { source { file("file1"); sheet("test") } }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Field set is not defined for entity type<entity> loading")
    }
}
