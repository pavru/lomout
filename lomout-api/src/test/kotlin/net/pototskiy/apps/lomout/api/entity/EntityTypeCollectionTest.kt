package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.entity.type.STRING
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EntityTypeCollectionTest {
    private val typeManager = EntityTypeManagerImpl()
    private val helper = ConfigBuildHelper(typeManager)

    @Test
    internal fun test() {
        val entities = EntityTypeCollection.Builder(helper).apply {
            entity("entity1", false) {
                attribute<STRING>("attr")
            }
            entity("entity2", false) {
                attribute<STRING>("attr")
            }
            entity("entity3", false) {
                attribute<STRING>("attr")
            }
        }.build()
        @Suppress("MagicNumber")
        assertThat(entities)
            .isInstanceOf(EntityTypeCollection::class.java)
            .hasSize(3)
        assertThat(entities.map { it.name })
            .containsAll(listOf("entity1", "entity2", "entity3"))

        assertThat(entities["test"]).isNull()
        assertThat(entities["entity2"]).isInstanceOf(EntityType::class.java)
        assertThat(entities["entity2"]?.name).isEqualTo("entity2")
        val type = entities["entity2"]
        val notInCollection = typeManager.createEntityType("entity", false)
        assertThat(entities.lastIndexOf(type)).isEqualTo(1)
        assertThat(entities.indexOf(type)).isEqualTo(1)
        assertThat(entities.contains(type)).isEqualTo(true)
        assertThat(entities.lastIndexOf(notInCollection)).isEqualTo(-1)
        assertThat(entities.indexOf(notInCollection)).isEqualTo(-1)
        assertThat(entities.contains(notInCollection)).isEqualTo(false)
    }
}