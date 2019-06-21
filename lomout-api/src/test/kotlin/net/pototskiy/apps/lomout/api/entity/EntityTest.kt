package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.EXPOSED_LOG_NAME
import net.pototskiy.apps.lomout.api.config.DatabaseConfig
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.helper.loadEntityAttributes
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.TEXT
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
import net.pototskiy.apps.lomout.api.plugable.AttributeBuilderPlugin
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("MagicNumber")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EntityTest {
    private lateinit var entityTypeManager: EntityTypeManagerImpl
    private lateinit var repository: EntityRepositoryInterface
    private lateinit var entityType: EntityType

    @Suppress("LongMethod")
    @BeforeAll
    internal fun setUpAll() {
        entityTypeManager = EntityTypeManagerImpl()
        repository = EntityRepository(
            DatabaseConfig.Builder().apply {
                name("test_lomout")
                server {
                    host("localhost")
                    port(3306)
                    user("root")
                    password("root")
                }
            }.build(),
            entityTypeManager,
            Level.ERROR
        )
        entityType = entityTypeManager.createEntityType("entity", false)
        @Suppress("UNCHECKED_CAST")
        entityTypeManager.initialAttributeSetup(
            entityType,
            AttributeCollection(
                listOf(
                    entityTypeManager.createAttribute(
                        "string_attr",
                        STRING::class,
                        key = false,
                        nullable = true,
                        auto = false,
                        builder = null,
                        reader = defaultReaders[STRING::class] as AttributeReader<out STRING>,
                        writer = defaultWriters[STRING::class] as AttributeWriter<out STRING>
                    ),
                    entityTypeManager.createAttribute(
                        "boolean_attr",
                        BOOLEAN::class,
                        key = false,
                        nullable = true,
                        auto = false,
                        builder = null,
                        reader = defaultReaders[BOOLEAN::class] as AttributeReader<out BOOLEAN>,
                        writer = defaultWriters[BOOLEAN::class] as AttributeWriter<out BOOLEAN>
                    ),
                    entityTypeManager.createAttribute(
                        "long_attr",
                        LONG::class,
                        key = false,
                        nullable = true,
                        auto = false,
                        builder = AttributeBuilderWithPlugin(TestBuilder::class),
                        reader = null,
                        writer = defaultWriters[LONG::class] as AttributeWriter<out LONG>
                    ),
                    entityTypeManager.createAttribute(
                        "nonnull_long_attr",
                        LONG::class,
                        key = false,
                        nullable = false,
                        auto = false,
                        builder = null,
                        reader = defaultReaders[LONG::class] as AttributeReader<out LONG>,
                        writer = defaultWriters[LONG::class] as AttributeWriter<out LONG>
                    )
                )
            )
        )
    }

    @AfterAll
    internal fun tearDownAll() {
        repository.close()
    }

    @BeforeEach
    internal fun setUp() {
        transaction { DbEntityTable.deleteAll() }
        Configurator.setLevel(EXPOSED_LOG_NAME, Level.TRACE)
    }

    @Test
    internal fun entityTypeTest() {
        assertThat(entityTypeManager["entity"]).isNotNull
        assertThat(entityTypeManager["entity"].attributes).hasSize(4)
    }

    @Test
    internal fun createEntityTest() {
        val entity = repository.create(entityType)
        assertThat(entity).isNotNull
        assertThat(entity.type).isEqualTo(entityType)
        assertThat(entity.id).isNotEqualTo(0)
        assertThat(entity.data).hasSize(0)
    }

    @Test
    internal fun setAttributeTest() {
        val entity = repository.create(entityType)
        assertThat(entity.data).hasSize(0)
        assertThat(entity["string_attr"]).isNull()
        assertThat(entity["boolean_attr"]).isNull()
        assertThat(entity["long_attr"]).isNull()
        var data = loadEntityAttributes(entity)
        assertThat(data.keys).hasSize(0)
        entity["string_attr"] = STRING("123")
        assertThat(entity["string_attr"]).isEqualTo(STRING("123"))
        assertThat(entity["boolean_attr"]).isNull()
        assertThat(entity["long_attr"]).isEqualTo(LONG(123L))
        data = loadEntityAttributes(entity)
        assertThat(data.keys).hasSize(1)
        assertThat(data[entity.type.getAttribute("string_attr")]).isEqualTo(STRING("123"))
        entity["string_attr"] = STRING("321")
        entity["string_attr"] = STRING("321")
        assertThat(entity["string_attr"]).isEqualTo(STRING("321"))
        assertThat(entity["boolean_attr"]).isNull()
        assertThat(entity["long_attr"]).isEqualTo(LONG(321L))
        data = loadEntityAttributes(entity)
        assertThat(data.keys).hasSize(1)
        assertThat(data[entity.type.getAttribute("string_attr")]).isEqualTo(STRING("321"))
        val entityFromCache = repository.get(entity.id)
        assertThat(entityFromCache).isSameAs(entity)
        assertThatThrownBy { entity["string_attr"] = TEXT("test") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Value is not compatible with the attribute's type.")
        assertThatThrownBy { entity["nonnull_long_attr"] = TEXT("test") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Value is not compatible with the attribute's type.")
        assertThatThrownBy { entity["nonnull_long_attr"] = null }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Trying to set null to non-nullable attribute")
        entity["string_attr"] = null
        assertThat(entity["string_attr"]).isNull()
        assertThat(entity["boolean_attr"]).isNull()
        assertThat(entity["long_attr"]).isNull()
        data = loadEntityAttributes(entity)
        assertThat(data.keys).hasSize(0)
    }

    class TestBuilder : AttributeBuilderPlugin<LONG>() {
        override fun build(entity: Entity): LONG? {
            val value = entity["string_attr"] as? STRING ?: return null
            return LONG(value.value.toLong())
        }
    }
}
