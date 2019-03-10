package net.pototskiy.apps.magemediation.api.config.loader

import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class LoadBuilderTest {
    private val typeManager = EntityTypeManager()
    private val entity = typeManager.createEntityType("entity", emptyList(), true)

    @Test
    internal fun validateFieldColumnsTest() {
        assertThatThrownBy {
            Load.Builder(typeManager, entity).apply {
                sourceFields {
                    main("test") {
                        field("f1") { column(0) }
                        field("f2") { column(0) }
                        field("f3") { column(1) }
                        field("f4") { column(1) }
                    }
                }
            }.build()
        }.isInstanceOf(ConfigException::class.java)
            .hasMessageContaining("Field columns<0, 1> are duplicated")
    }

    @Test
    internal fun sourcesNotDefinedTest() {
        assertThatThrownBy {
            Load.Builder(typeManager, entity).apply {
                sourceFields {
                    main("test") {
                        field("f1") { column(1) }
                        field("f2") { column(2) }
                    }
                }
            }.build()
        }.isInstanceOf(ConfigException::class.java)
            .hasMessageContaining("Source files are not defined for entity type<entity> loading")
    }

//    @Test
//    internal fun sourceFieldsNotDefinedTest() {
//        assertThatThrownBy {
//            Load.Builder(typeManager, entity).apply {
//                fromSources { source { file("file1"); sheet("test") } }
//            }.build()
//        }.isInstanceOf(ConfigException::class.java)
//            .hasMessageContaining("Source files are not defined for entity type<entity> loading")
//    }
}
