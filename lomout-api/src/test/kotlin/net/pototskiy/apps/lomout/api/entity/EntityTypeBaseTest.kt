package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
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
        val eType = typeManager.createEntityType(
            "test1",
            emptyList(),
            false
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
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
    }

    @Test
    internal fun createOpenAndRefineTest() {
        val eType = typeManager.createEntityType(
            "test1",
            emptyList(),
            true
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
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
        val test1 = typeManager.createEntityType(
            "test1",
            emptyList(),
            true
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        val test2 = typeManager.createEntityType(
            "test2",
            listOf(ParentEntityType(test1)),
            true
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr4))) }
        assertThat(test2.attributes)
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr2, attr4))
        assertThat(test2["attr2"]).isEqualTo(attr2)
        assertThat(typeManager["test2"]).isEqualTo(test2)
        try {
            test2.checkAttributeDefined(attr1)
            assertThat(true).isEqualTo(true)
        } catch (e: Exception) {
            assertThat(false).isEqualTo(true)
        }
        assertThatThrownBy { test2.checkAttributeDefined(attr3) }.isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { test2.checkAttributeDefined(attr5) }.isInstanceOf(AppConfigException::class.java)
        assertThat(test2.getAttributeOrNull("attr1")).isEqualTo(attr1)
        assertThat(test2.getAttributeOrNull("attr3")).isNull()
        assertThat(test2.getAttributeOrNull("attr4")).isEqualTo(attr4)
        assertThat(test2.getAttribute("attr1")).isEqualTo(attr1)
        assertThatThrownBy { test2.getAttribute("attr3") }.isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { test2.getAttribute("attr5") }.isInstanceOf(AppConfigException::class.java)
        typeManager.addEntityAttribute(test2, attr3)
        assertThat(test2["attr3"]).isEqualTo(attr3)
    }

    @Test
    internal fun createWithInheritanceIncludeTest() {
        val test1 = typeManager.createEntityType(
            "test1",
            emptyList(),
            true
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2, attr3))) }
        val test2 = typeManager.createEntityType(
            "test2",
            listOf(ParentEntityType(test1, AttributeCollection(listOf(attr1, attr3)))),
            false
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr4))) }
        val test3 = typeManager.createEntityType(
            "test3",
            listOf(
                ParentEntityType(test2, AttributeCollection(listOf(attr4, attr3))),
                ParentEntityType(test1, AttributeCollection(listOf(attr2)))
            ),
            false
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr5, attr6))) }
        assertThat(test2.attributes)
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr3, attr4))
        assertThat(test3.attributes)
            .hasSize(5)
            .containsExactlyInAnyOrderElementsOf(listOf(attr4, attr3, attr2, attr5, attr6))
    }

    @Test
    internal fun createWithInheritanceExcludeTest() {
        val test1 = typeManager.createEntityType(
            "test1",
            emptyList(),
            true
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2, attr3))) }
        val test2 = typeManager.createEntityType(
            "test2",
            listOf(ParentEntityType(test1, null, AttributeCollection(listOf(attr2)))),
            false
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr4))) }
        val test3 = typeManager.createEntityType(
            "test3",
            listOf(
                ParentEntityType(test2, null, AttributeCollection(listOf(attr1, attr2))),
                ParentEntityType(test1, null, AttributeCollection(listOf(attr1, attr3)))
            ),
            false
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr5, attr6))) }
        assertThat(test2.attributes)
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr3, attr4))
        assertThat(test3.attributes)
            .hasSize(5)
            .containsExactlyInAnyOrderElementsOf(listOf(attr4, attr3, attr2, attr5, attr6))
    }

    @Test
    internal fun removeEntityTypeTest() {
        val eType = typeManager.createEntityType(
            "test1",
            emptyList(),
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
            emptyList(),
            true
        ).also { typeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        assertThatThrownBy { typeManager.addEntityAttribute(eType, attr1) }
            .isInstanceOf(AppConfigException::class.java)
        assertThatThrownBy { typeManager.addEntityAttribute(eType, dupAttr1) }
            .isInstanceOf(AppConfigException::class.java)
    }

    @Test
    internal fun equalsTest() {
        val type1 = typeManager.createEntityType("type1", emptyList(), false)
        val type2 = typeManager.createEntityType("type2", emptyList(), false)
        val type3 = typeManager.createEntityType("type1", emptyList(), false)
        assertThat(type1 == type1).isEqualTo(true)
        assertThat(type1 == type2).isEqualTo(false)
        assertThat(type1 == type3).isEqualTo(true)
        assertThat(type1 == Any()).isEqualTo(false)
    }
}
