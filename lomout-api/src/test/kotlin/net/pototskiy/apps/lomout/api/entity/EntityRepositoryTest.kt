package net.pototskiy.apps.lomout.api.entity

import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.TIMESTAMP
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.DatabaseConfig
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface.Companion.REMOVED_ENTITY
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.STRINGLIST
import net.pototskiy.apps.lomout.api.entity.type.table
import org.apache.logging.log4j.Level
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock

@Suppress("TooManyFunctions", "MagicNumber")
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class EntityRepositoryTest {
    private lateinit var typeManager: EntityTypeManager
    private lateinit var repository: EntityRepositoryInterface
    private lateinit var type: EntityType
    private lateinit var keyAttr: Attribute<STRING>
    private lateinit var valueAttr: Attribute<STRING>
    private lateinit var listAttr: Attribute<STRINGLIST>
    private val dbConfig = DatabaseConfig.Builder("test_lomout").apply {
        server {
            host("127.0.0.1")
            port(3306)
            user("root")
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                password("")
            } else {
                password("root")
            }
        }
    }.build()

    @BeforeEach
    internal fun setUp() {
        typeManager = EntityTypeManagerImpl()
        val helper = ConfigBuildHelper(typeManager)
        repository = EntityRepository(dbConfig, typeManager, Level.ERROR)
        type = EntityType.Builder(helper, "type", false).apply {
            attribute<STRING>("key") { key() }
            attribute<STRING>("value") { nullable() }
            attribute<STRINGLIST>("list") { nullable() }
        }.build()
        @Suppress("UNCHECKED_CAST")
        keyAttr = type["key"] as Attribute<STRING>
        @Suppress("UNCHECKED_CAST")
        valueAttr = type["value"] as Attribute<STRING>
        @Suppress("UNCHECKED_CAST")
        listAttr = type["list"] as Attribute<STRINGLIST>
        repository.getIDs(type).forEach { repository.delete(it) }
        mapOf(
            "1" to "value1",
            "2" to "value2",
            "3" to "value3",
            "4" to "value4"
        ).forEach {
            val entity = repository.create(type)
            entity[keyAttr] = STRING(it.key)
            entity[valueAttr] = STRING(it.value)
            repository.update(entity)
        }
    }

    @AfterEach
    internal fun tearDown() {
        repository.close()
    }

    @Test
    fun getSetCacheStrategyTest() {
        assertThat(repository.cacheStrategy).isEqualTo(EntityRepositoryInterface.CacheStrategy.LOADER)
        repository.cacheStrategy = EntityRepositoryInterface.CacheStrategy.MEDIATOR
        assertThat(repository.cacheStrategy).isEqualTo(EntityRepositoryInterface.CacheStrategy.MEDIATOR)
    }

    @Test
    fun createTest() {
        val entity = repository.create(type)
        assertThat(entity).isNotNull
        for (i in 1..500) {
            entity[valueAttr] = null
            entity[keyAttr] = STRING(i.toString())
            entity[valueAttr] = STRING("value$i")
        }
    }

    @Test
    fun updateTest() {
        val entity = repository.get(type).firstOrNull()
        assertThat(entity).isNotNull
        entity as Entity
        assertThat(entity.currentStatus).isEqualTo(EntityStatus.CREATED)
        entity.currentStatus = EntityStatus.UPDATED
        assertThat(transaction {
            DbEntityTable
                .slice(DbEntityTable.currentStatus)
                .select { DbEntityTable.id eq entity.id }
                .map { it[DbEntityTable.currentStatus] }
        }.first()).isEqualTo(EntityStatus.CREATED)
        repository.update(entity)
        assertThat(transaction {
            DbEntityTable
                .slice(DbEntityTable.currentStatus)
                .select { DbEntityTable.id eq entity.id }
                .map { it[DbEntityTable.currentStatus] }
        }.first()).isEqualTo(EntityStatus.UPDATED)
    }

    @Test
    fun deleteTest() {
        assertThat(transaction {
            DbEntityTable.select { DbEntityTable.entityType eq type }.count()
        }).isEqualTo(4)
        val entities = repository.get(type)
        repository.delete(entities[3])
        assertThat(transaction {
            DbEntityTable.select { DbEntityTable.entityType eq type }.count()
        }).isEqualTo(3)
        repository.delete(entities[2].id)
        assertThat(transaction {
            DbEntityTable.select { DbEntityTable.entityType eq type }.count()
        }).isEqualTo(2)
    }

    @Test
    fun getByTypeTest() {
        assertThat(repository.get(type).map { (it["key"] as STRING).value })
            .hasSize(4)
            .containsAll(listOf("1", "2", "3", "4"))
        repository.close()
        repository = EntityRepository(dbConfig, typeManager, Level.ERROR, 10)
        assertThat(repository.get(type).map { (it["key"] as STRING).value })
            .hasSize(4)
            .containsAll(listOf("1", "2", "3", "4"))
        assertThat(repository.get(type, EntityStatus.REMOVED))
            .hasSize(0)
    }

    @Test
    fun getById() {
        val ids = transaction {
            DbEntityTable
                .slice(DbEntityTable.id)
                .select { DbEntityTable.entityType eq type }
                .orderBy(DbEntityTable.id, SortOrder.ASC)
                .map { it[DbEntityTable.id] }
        }
        val entity = repository.get(ids[0])
        assertThat(entity).isNotNull
        entity as Entity
        assertThat(entity["key"]).isEqualTo(STRING("1"))
        assertThat(entity["value"]).isEqualTo(STRING("value1"))
        assertThat(repository.get(ids[0], EntityStatus.REMOVED)).isNull()
        assertThat(repository.get(EntityID(-1, DbEntityTable))).isNull()
        repository.cacheStrategy = EntityRepositoryInterface.CacheStrategy.MEDIATOR
        assertThat(repository.get(ids[0])).isNotNull
        repository.cacheStrategy = EntityRepositoryInterface.CacheStrategy.PRINTER
        assertThat(repository.get(ids[0])).isNotNull
    }

    @Test
    fun getByAttribute() {
        val ids = transaction {
            DbEntityTable
                .slice(DbEntityTable.id)
                .select { DbEntityTable.entityType eq type }
                .orderBy(DbEntityTable.id, SortOrder.ASC)
                .map { it[DbEntityTable.id] }
        }
        val entity = repository.get(type, mapOf(keyAttr as Attribute<*> to STRING("2")))
        assertThat(entity).isNotNull
        entity as Entity
        assertThat(entity.id).isEqualTo(ids[1])
        assertThat(repository.get(type, mapOf(keyAttr as Attribute<*> to STRING("2")))).isNotNull
        assertThat(repository.get(type, mapOf(keyAttr as Attribute<*> to STRING("2")),*REMOVED_ENTITY))
            .isNull()
        assertThat(repository.get(type, mapOf(keyAttr as Attribute<*> to STRING("5"))))
            .isNull()
        (repository as EntityRepository).clearCache()
        assertThat(repository.get(type, mapOf(keyAttr as Attribute<*> to STRING("2")))).isNotNull
    }

    @Test
    fun preloadByIdTest() {
        val ids = transaction {
            DbEntityTable
                .slice(DbEntityTable.id)
                .select { DbEntityTable.entityType eq type }
                .orderBy(DbEntityTable.id, SortOrder.ASC)
                .map { it[DbEntityTable.id] }
        }
        (repository as EntityRepository).clearCache()
        runBlocking {
            repository.preload(ids[0], false)
            repository.preload(ids[1], true)
            repository.preload(EntityID(-1, DbEntityTable), true)
        }
        assertThat(repository.get(ids[0])).isNotNull
        assertThat(repository.get(ids[0])!!["key"]).isEqualTo(STRING("1"))
        assertThat(repository.get(ids[3])).isNotNull
        assertThat(repository.get(ids[3])!!["key"]).isEqualTo(STRING("4"))
    }

    @Test
    fun preloadByAttribute() {
        val ids = transaction {
            DbEntityTable
                .slice(DbEntityTable.id)
                .select { DbEntityTable.entityType eq type }
                .orderBy(DbEntityTable.id, SortOrder.ASC)
                .map { it[DbEntityTable.id] }
        }
        (repository as EntityRepository).clearCache()
        runBlocking {
            repository.preload(type, mapOf(keyAttr as AnyTypeAttribute to STRING("3")))
            repository.preload(type, mapOf(keyAttr as AnyTypeAttribute to STRING("3")))
            repository.preload(type, mapOf(keyAttr as AnyTypeAttribute to STRING("20")))
        }
        assertThat(repository.get(ids[2])).isNotNull
        assertThat(repository.get(ids[2])!!["value"]).isEqualTo(STRING("value3"))
    }

    @Test
    fun getIDsTest() {
        val ids = transaction {
            DbEntityTable
                .slice(DbEntityTable.id)
                .select { DbEntityTable.entityType eq type }
                .orderBy(DbEntityTable.id, SortOrder.ASC)
                .map { it[DbEntityTable.id] }
        }
        assertThat(repository.getIDs(type))
            .hasSize(4)
            .containsAll(ids)
    }

    @Test
    fun getPagedIDsTest() {
        val ids = transaction {
            DbEntityTable
                .slice(DbEntityTable.id)
                .select { DbEntityTable.entityType eq type }
                .orderBy(DbEntityTable.id, SortOrder.ASC)
                .map { it[DbEntityTable.id] }
        }
        assertThat(repository.getIDs(type, 2, 0))
            .hasSize(2)
            .containsExactlyElementsOf(ids.slice(0..1))
        assertThat(repository.getIDs(type, 2, 1))
            .hasSize(2)
            .containsExactlyElementsOf(ids.slice(2..3))
    }

    @Test
    fun resetTouchFlagTest() {
        assertThat(repository.get(type).all { it.touchedInLoading }).isEqualTo(true)
        repository.resetTouchFlag(type)
        assertThat(repository.get(type).all { !it.touchedInLoading }).isEqualTo(true)
    }

    @Test
    fun markEntitiesAsRemoved() {
        val entities = repository.get(type)
        assertThat(entities.all { it.currentStatus != EntityStatus.REMOVED }).isEqualTo(true)
        entities[1].touchedInLoading = false
        repository.update(entities[1])
        repository.markEntitiesAsRemoved(type)
        assertThat(repository.get(type)[1].currentStatus).isEqualTo(EntityStatus.REMOVED)
    }

    @Test
    fun updateAbsentDays() {
        val entities = repository.get(type)
        assertThat(entities.all { it.absentDays == 0 }).isEqualTo(true)
        repository.markEntitiesAsRemoved(type)
        entities[1].touchedInLoading = false
        repository.update(entities[1])
        repository.markEntitiesAsRemoved(type)
        entities[1].removed = TIMESTAMP.minusDays(5)
        repository.update(entities[1])
        repository.updateAbsentDays(type)
        assertThat(repository.get(type)[1].absentDays).isEqualTo(5)
    }

    @Test
    fun removeOldEntities() {
        val entities = repository.get(type)
        assertThat(entities.all { it.absentDays == 0 }).isEqualTo(true)
        repository.markEntitiesAsRemoved(type)
        entities[1].touchedInLoading = false
        repository.update(entities[1])
        repository.markEntitiesAsRemoved(type)
        entities[1].removed = TIMESTAMP.minusDays(5)
        repository.update(entities[1])
        repository.updateAbsentDays(type)
        assertThat(repository.get(type)[1].absentDays).isEqualTo(5)
        repository.removeOldEntities(type, 4)
        assertThat(transaction {
            DbEntityTable
                .select { DbEntityTable.entityType eq type }
                .count()
        }).isEqualTo(3)
    }

    @Test
    fun createUpdateDeleteAttribute() {
        val entity = repository.create(type)
        val table = listAttr.type.table
        assertThat(transaction {
            table
                .select { (table.code eq listAttr.name) and (table.owner eq entity.id) }
                .count()
        }).isEqualTo(0)
        entity[listAttr] = STRINGLIST(listOf(STRING("11"), STRING("12"), STRING("13")))
        assertThat(transaction {
            table
                .select { (table.code eq listAttr.name) and (table.owner eq entity.id) }
                .count()
        }).isEqualTo(3)
        entity[listAttr] = STRINGLIST(listOf(STRING("11"), STRING("12"), STRING("13"), STRING("14")))
        assertThat(transaction {
            table
                .select { (table.code eq listAttr.name) and (table.owner eq entity.id) }
                .count()
        }).isEqualTo(4)
        entity[listAttr] = null
        assertThat(transaction {
            table
                .select { (table.code eq listAttr.name) and (table.owner eq entity.id) }
                .count()
        }).isEqualTo(0)
    }

}
