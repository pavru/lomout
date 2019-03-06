package net.pototskiy.apps.magemediation.api.entity

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Suppress("MagicNumber", "TooGenericExceptionCaught")
@DisplayName("EntityType base functionality test")
internal class EntityTypeBaseTest {

    private lateinit var attr1: Attribute<*>
    private lateinit var attr2: Attribute<*>
    private lateinit var attr3: Attribute<*>
    private lateinit var attr4: Attribute<*>
    private lateinit var attr5: Attribute<*>
    private lateinit var attr6: Attribute<*>

    @BeforeEach
    internal fun setUp() {
        EntityTypeManager.currentManager = EntityTypeManager()
        attr1 = EntityTypeManager.createAttribute("attr1", StringType::class)
        attr2 = EntityTypeManager.createAttribute("attr2", StringType::class)
        attr3 = EntityTypeManager.createAttribute("attr3", StringType::class)
        attr4 = EntityTypeManager.createAttribute("attr4", StringType::class)
        attr5 = EntityTypeManager.createAttribute("attr5", StringType::class)
        attr6 = EntityTypeManager.createAttribute("attr6", StringType::class)
    }

    @Test
    internal fun createEntityTest() {
        val eType = EntityTypeManager.createEntityType(
            "test1",
            emptyList(),
            false
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        assertThat(eType).isNotNull
        assertThat(eType.name).isEqualTo("test1")
        assertThat(eType.attributes)
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr2))
        assertThat(eType.open).isFalse()
        assertThatThrownBy {
            EntityTypeManager.addEntityAttribute(
                eType,
                attr3
            )
        }.isInstanceOf(DatabaseException::class.java)
        assertThat(eType["attr2"]).isEqualTo(attr2)
        assertThat(EntityTypeManager["test1"]).isEqualTo(eType)
        try {
            eType.checkAttributeDefined(attr1)
            assertThat(true).isTrue()
        } catch (e: Exception) {
            assertThat(false).isTrue()
        }
        assertThatThrownBy { eType.checkAttributeDefined(attr3) }.isInstanceOf(DatabaseException::class.java)
        assertThatThrownBy { eType.checkAttributeDefined(attr4) }.isInstanceOf(DatabaseException::class.java)
        assertThat(eType.getAttributeOrNull("attr1")).isEqualTo(attr1)
        assertThat(eType.getAttributeOrNull("attr3")).isNull()
        assertThat(eType.getAttributeOrNull("attr4")).isNull()
        assertThat(eType.getAttribute("attr1")).isEqualTo(attr1)
        assertThatThrownBy { eType.getAttribute("attr3") }.isInstanceOf(DatabaseException::class.java)
        assertThatThrownBy { eType.getAttribute("attr4") }.isInstanceOf(DatabaseException::class.java)
    }

    @Test
    internal fun createOpenAndRefineTest() {
        val eType = EntityTypeManager.createEntityType(
            "test1",
            emptyList(),
            true
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        assertThat(eType.attributes)
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr2))
        EntityTypeManager.addEntityAttribute(eType, attr3)
        assertThat(eType.attributes)
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr2, attr3))
    }

    @Test
    internal fun createEntityWithInheritanceTest() {
        val test1 = EntityTypeManager.createEntityType(
            "test1",
            emptyList(),
            true
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2))) }
        val test2 = EntityTypeManager.createEntityType(
            "test2",
            listOf(ParentEntityType(test1)),
            false
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr4))) }
        assertThat(test2.attributes)
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr2, attr4))
        assertThat(test2["attr2"]).isEqualTo(attr2)
        assertThat(EntityTypeManager["test2"]).isEqualTo(test2)
        try {
            test2.checkAttributeDefined(attr1)
            assertThat(true).isTrue()
        } catch (e: Exception) {
            assertThat(false).isTrue()
        }
        assertThatThrownBy { test2.checkAttributeDefined(attr3) }.isInstanceOf(DatabaseException::class.java)
        assertThatThrownBy { test2.checkAttributeDefined(attr5) }.isInstanceOf(DatabaseException::class.java)
        assertThat(test2.getAttributeOrNull("attr1")).isEqualTo(attr1)
        assertThat(test2.getAttributeOrNull("attr3")).isNull()
        assertThat(test2.getAttributeOrNull("attr4")).isEqualTo(attr4)
        assertThat(test2.getAttribute("attr1")).isEqualTo(attr1)
        assertThatThrownBy { test2.getAttribute("attr3") }.isInstanceOf(DatabaseException::class.java)
        assertThatThrownBy { test2.getAttribute("attr5") }.isInstanceOf(DatabaseException::class.java)
        EntityTypeManager.addEntityAttribute(test1, attr3)
        assertThat(test2["attr3"]).isEqualTo(attr3)
    }

    @Test
    internal fun createWithInheritanceIncludeTest() {
        val test1 = EntityTypeManager.createEntityType(
            "test1",
            emptyList(),
            true
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2, attr3))) }
        val test2 = EntityTypeManager.createEntityType(
            "test2",
            listOf(ParentEntityType(test1, AttributeCollection(listOf(attr1, attr3)))),
            false
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr4))) }
        val test3 = EntityTypeManager.createEntityType(
            "test3",
            listOf(
                ParentEntityType(test2, AttributeCollection(listOf(attr4, attr3))),
                ParentEntityType(test1, AttributeCollection(listOf(attr2)))
            ),
            false
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr5, attr6))) }
        assertThat(test2.attributes)
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr3, attr4))
        assertThat(test3.attributes)
            .hasSize(5)
            .containsExactlyInAnyOrderElementsOf(listOf(attr4, attr3, attr2, attr5, attr6))
    }

    @Test
    internal fun createWithInheritanceExcludeTest() {
        val test1 = EntityTypeManager.createEntityType(
            "test1",
            emptyList(),
            true
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr1, attr2, attr3))) }
        val test2 = EntityTypeManager.createEntityType(
            "test2",
            listOf(ParentEntityType(test1, null, AttributeCollection(listOf(attr2)))),
            false
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr4))) }
        val test3 = EntityTypeManager.createEntityType(
            "test3",
            listOf(
                ParentEntityType(test2, null, AttributeCollection(listOf(attr1, attr2))),
                ParentEntityType(test1, null, AttributeCollection(listOf(attr1, attr3)))
            ),
            false
        ).also { EntityTypeManager.initialAttributeSetup(it, AttributeCollection(listOf(attr5, attr6))) }
        assertThat(test2.attributes)
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(listOf(attr1, attr3, attr4))
        assertThat(test3.attributes)
            .hasSize(5)
            .containsExactlyInAnyOrderElementsOf(listOf(attr4, attr3, attr2, attr5, attr6))
    }
}
