package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber", "TooGenericExceptionCaught")
@DisplayName("EntityType base functionality test")
@Execution(ExecutionMode.CONCURRENT)
internal class EntityTypeBaseTest {

    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)
    private lateinit var attr1: Attribute<*>
    private lateinit var dupAttr1: Attribute<*>
    private lateinit var attr2: Attribute<*>
    private lateinit var attr3: Attribute<*>
    private lateinit var attr4: Attribute<*>
    private lateinit var attr5: Attribute<*>
    private lateinit var attr6: Attribute<*>

    @BeforeEach
    internal fun setUp() {
        @Suppress("UNCHECKED_CAST")
        attr1 = typeManager.createAttribute(
            "attr1", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
        @Suppress("UNCHECKED_CAST")
        dupAttr1 = typeManager.createAttribute(
            "attr1", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
        @Suppress("UNCHECKED_CAST")
        attr2 = typeManager.createAttribute(
            "attr2", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
        @Suppress("UNCHECKED_CAST")
        attr3 = typeManager.createAttribute(
            "attr3", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
        @Suppress("UNCHECKED_CAST")
        attr4 = typeManager.createAttribute(
            "attr4", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
        @Suppress("UNCHECKED_CAST")
        attr5 = typeManager.createAttribute(
            "attr5", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
        @Suppress("UNCHECKED_CAST")
        attr6 = typeManager.createAttribute(
            "attr6", STRING::class,
            builder = null,
            reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
            writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
        )
    }

    @Test
    internal fun createEntityTest() {
        val eType = typeManager.createEntityType("test1", false)
            .also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        assertThat(eType).isNotNull
        assertThat(eType.name).isEqualTo("test1")
        assertThat(eType.attributes)
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr2))
        assertThat(eType.open).isEqualTo(false)
        assertThatThrownBy {
            typeManager.addEntityAttribute(
                eType,
                attr3
            )
        }.isInstanceOf(AppConfigException::class.java)
        assertThat(eType["attr2"]).isEqualTo(attr2)
        assertThat(typeManager["test1"]).isEqualTo(eType)
        try {
            eType.checkAttributeDefined(attr1)
            assertThat(true).isEqualTo(true)
        } catch (e: Exception) {
            assertThat(false).isEqualTo(true)
        }
        assertThatThrownBy { eType.checkAttributeDefined(attr3) }.isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { eType.checkAttributeDefined(attr4) }.isInstanceOf(AppConfigException::class.java)
        assertThat(eType.getAttributeOrNull("attr1")).isEqualTo(attr1)
        assertThat(eType.getAttributeOrNull("attr3")).isNull()
        assertThat(eType.getAttributeOrNull("attr4")).isNull()
        assertThat(eType.getAttribute("attr1")).isEqualTo(attr1)
        assertThatThrownBy { eType.getAttribute("attr3") }.isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { eType.getAttribute("attr4") }.isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy {
            typeManager.initialAttributeSetup(eType, AttributeCollection(listOf(attr1, attr2)))
        }.isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { typeManager.createEntityType("test1", false) }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Entity 'test1' already exists.")
    }

    @Test
    internal fun createOpenAndRefineTest() {
        val eType = typeManager.createEntityType("test1", true)
            .also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        assertThat(eType.attributes)
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr2))
        typeManager.addEntityAttribute(eType, attr3)
        assertThat(eType.attributes)
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr2, attr3))
    }

    @Test
    internal fun createEntityWithInheritanceTest() {
        val test1 = typeManager.createEntityType("test1", true)
            .also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        val test2 = typeManager.createEntityType("test2", true).also { type ->
            typeManager.initialAttributeSetup(
                type,
                AttributeCollection(listOf(attr4).plus(
                    test1.mainAttributes.map {
                        typeManager.createAttribute(
                            it.name, it.type, it.key, it.nullable, it.auto, it.builder, it.reader, it.writer
                        )
                    }
                ))
            )
        }
        assertThat(test2.attributes.map { it.name })
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf("attr1", "attr2", "attr4"))
        assertThat(test2["attr2"].name).isEqualTo(attr2.name)
        assertThat(typeManager["test2"]).isEqualTo(test2)
        try {
            test2.checkAttributeDefined(attr1)
            assertThat(true).isEqualTo(true)
        } catch (e: Exception) {
            assertThat(false).isEqualTo(true)
        }
        assertThatThrownBy { test2.checkAttributeDefined(attr3) }.isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { test2.checkAttributeDefined(attr5) }.isInstanceOf(AppConfigException::class.java)
        assertThat(test2.getAttributeOrNull("attr1")?.name).isEqualTo(attr1.name)
        assertThat(test2.getAttributeOrNull("attr3")).isNull()
        assertThat(test2.getAttributeOrNull("attr4")?.name).isEqualTo(attr4.name)
        assertThat(test2.getAttribute("attr1").name).isEqualTo(attr1.name)
        assertThatThrownBy { test2.getAttribute("attr3") }.isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { test2.getAttribute("attr5") }.isInstanceOf(AppConfigException::class.java)
        typeManager.addEntityAttribute(test2, attr3)
        assertThat(test2["attr3"]).isEqualTo(attr3)
    }

    @Test
    internal fun createWithInheritanceIncludeTest() {
        @Suppress("UNUSED_VARIABLE")
        val test1 = typeManager.createEntityType("test1", true)
            .also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2, attr3))) }
        val test2 = typeManager.createEntityType(
            "test2",
            false
        ).also {
            typeManager.initialAttributeSetup(
                it,
                AttributeCollection(
                    listOf(attr4).plus(
                        EntityType.Builder.CopyAttributeListBuilder(helper, "test1").apply {
                            exclude("attr2")
                        }.build()
                    )
                )
            )
        }
        val test3 = typeManager.createEntityType("test3", false).also {
            typeManager.initialAttributeSetup(
                it,
                AttributeCollection(
                    listOf(attr5, attr6).plus(
                        EntityType.Builder.CopyAttributeListBuilder(helper, "test2").apply {
                            exclude("attr1", "attr2")
                        }.build().plus(
                            EntityType.Builder.CopyAttributeListBuilder(helper, "test1").apply {
                                exclude("attr1", "attr3")
                            }.build()
                        )
                    )
                )
            )
        }
        assertThat(test2.attributes.map { it.name })
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf("attr1", "attr3", "attr4"))
        assertThat(test3.attributes.map { it.name })
            .hasSize(5)
            .containsExactlyInAnyOrderElementsOf(listOf("attr4", "attr3", "attr2", "attr5", "attr6"))
    }

    @Test
    internal fun createWithInheritanceExcludeTest() {
        @Suppress("UNUSED_VARIABLE")
        val test1 = typeManager.createEntityType(
            "test1",
            true
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2, attr3))) }
        val test2 = typeManager.createEntityType("test2", false).also {
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(attr4).plus(
                        EntityType.Builder.CopyAttributeListBuilder(helper, "test1").apply {
                            exclude("attr2")
                        }.build()
                    )
                )
            )
        }
        val test3 = typeManager.createEntityType("test3", false).also {
            typeManager.initialAttributeSetup(
                it, AttributeCollection(
                    listOf(attr5, attr6).plus(
                        EntityType.Builder.CopyAttributeListBuilder(helper, "test2").apply {
                            exclude("attr1")
                            exclude("attr2")
                        }.build().plus(
                            EntityType.Builder.CopyAttributeListBuilder(helper, "test1").apply {
                                exclude("attr1", "attr3")
                            }.build()
                        )
                    )
                )
            )
        }
        assertThat(test2.attributes.map { it.name })
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf("attr1", "attr3", "attr4"))
        assertThat(test3.attributes.map { it.name })
            .hasSize(5)
            .containsExactlyInAnyOrderElementsOf(listOf("attr4", "attr3", "attr2", "attr5", "attr6"))
    }

    @Test
    internal fun removeEntityTypeTest() {
        val eType = typeManager.createEntityType(
            "test1",
            false
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        assertThat(typeManager["test1"]).isEqualTo(eType)
        typeManager.removeEntityType(eType)
        assertThatThrownBy { typeManager["test1"] }.isInstanceOf(AppConfigException::class.java)
    }

    @Test
    internal fun tryToAddAlreadyAssignedAttributeTest() {
        val eType = typeManager.createEntityType(
            "test1",
            true
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        assertThatThrownBy { typeManager.addEntityAttribute(eType, attr1) }
            .isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { typeManager.addEntityAttribute(eType, dupAttr1) }
            .isInstanceOf(AppConfigException::class.java)
    }

    @Test
    internal fun equalsTest() {
        val type1 = typeManager.createEntityType("type1", false)
        val type2 = typeManager.createEntityType("type2", false)
        assertThat(type1 == type1).isEqualTo(true)
        assertThat(type1 == type2).isEqualTo(false)
        assertThat(type1 == Any()).isEqualTo(false)
    }
}
