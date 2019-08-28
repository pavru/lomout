/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.pototskiy.apps.lomout.api.entity

import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.script.DatabaseConfig
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.Documents
import net.pototskiy.apps.lomout.api.document.Key
import org.apache.logging.log4j.Level
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import org.litote.kmongo.eq
import org.litote.kmongo.fields
import org.litote.kmongo.include
import kotlin.reflect.KClass

@Suppress("TooManyFunctions", "MagicNumber")
@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
internal class EntityRepositoryTest {
    private lateinit var repository: EntityRepositoryInterface
    private lateinit var type: KClass<out Document>
    private lateinit var keyAttr: DocumentMetadata.Attribute
    private lateinit var valueAttr: DocumentMetadata.Attribute
    private lateinit var listAttr: DocumentMetadata.Attribute
    private val dbConfig = DatabaseConfig.Builder("lomout_test").apply {
        server {
            host("127.0.0.1")
            port(27017)
            user("root")
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                password("")
            } else {
                password("root")
            }
        }
    }.build()
    private lateinit var documents: Documents

    class EntityType : Document() {
        @Key
        var key: String = ""
        var value: String? = null
        var list: List<String>? = null

        companion object : DocumentMetadata(EntityType::class)
    }

    @BeforeEach
    internal fun setUp() {
        documents = Documents("lomout_test")
        repository = EntityRepository(dbConfig, Level.ERROR)
        type = EntityType::class
        keyAttr = EntityType.attributes.getValue("key")
        valueAttr = EntityType.attributes.getValue("value")
        listAttr = EntityType.attributes.getValue("list")
        repository.getIDs(type, includeDeleted = true).forEach { repository.delete(type, it) }
        mapOf(
            "1" to "value1",
            "2" to "value2",
            "3" to "value3",
            "4" to "value4"
        ).forEach {
            val entity = repository.create(type) as EntityType
            entity.key = it.key
            entity.value = it.value
            repository.update(entity)
        }
    }

    @AfterEach
    internal fun tearDown() {
        repository.close()
        documents.close()
    }

    @Test
    internal fun partialGetTest() {
        val doc = EntityType().apply {
            key = "key-value-123456"
            value = "test-string-value"
            list = listOf("1", "2", "3")
        }
        repository.update(doc)
        var read = repository.get(EntityType::class, listOf(EntityType::key), Document::_id eq doc._id) as? EntityType
        assertThat(read?.key).isEqualTo("key-value-123456")
        assertThat(read?.value).isNull()
        assertThat(read?.list).isNull()
        read = repository.get(EntityType::class, listOf(EntityType::value), Document::_id eq doc._id, includeDeleted = true) as? EntityType
        assertThat(read?.key).isEqualTo("")
        assertThat(read?.value).isEqualTo("test-string-value")
        assertThat(read?.list).isNull()
        read = repository.get(
            EntityType::class,
            listOf(EntityType::value, EntityType::list),
            Document::_id eq doc._id
        ) as? EntityType
        assertThat(read?.key).isEqualTo("")
        assertThat(read?.value).isEqualTo("test-string-value")
        assertThat(read?.list).hasSize(3).containsAll(listOf("1", "2", "3"))
    }

    @Test
    fun createTest() {
        val entity = repository.create(type) as EntityType
        assertThat(entity).isNotNull
        for (i in 1..500) {
            entity.value = null
            entity.key = i.toString()
            entity.value = "value$i"
        }
    }

    @Test
    fun updateTest() {
        val previousTimestamp = Documents.timestamp
        repository.close()
        runBlocking { kotlinx.coroutines.delay(1500) }
        repository = EntityRepository(dbConfig, Level.ERROR)
        val entity = repository.get(type, includeDeleted = true).firstOrNull()
        assertThat(entity).isNotNull
        entity as EntityType
        assertThat(entity.createTime).isEqualTo(previousTimestamp)
        entity.markUpdated()
        var fromDb = documents.getOne(EntityType::class, entity._id)!!
        assertThat(fromDb.createTime).isEqualTo(previousTimestamp)
        assertThat(fromDb.updateTime).isEqualTo(previousTimestamp)
        assertThat(fromDb.toucheTime).isEqualTo(previousTimestamp)
        repository.update(entity)
        fromDb = documents.getOne(EntityType::class, entity._id)!!
        assertThat(fromDb.createTime).isEqualTo(previousTimestamp)
        assertThat(fromDb.updateTime).isEqualTo(Documents.timestamp)
        assertThat(fromDb.toucheTime).isEqualTo(Documents.timestamp)
    }

    @Test
    fun deleteTest() {
        assertThat(documents.getCollection(EntityType::class).countDocuments()).isEqualTo(4)
        val entities = repository.get(type, includeDeleted = true)
        repository.delete(entities[3])
        assertThat(documents.getCollection(EntityType::class).countDocuments()).isEqualTo(3)
        repository.delete(EntityType::class, entities[2]._id)
        assertThat(documents.getCollection(EntityType::class).countDocuments()).isEqualTo(2)
    }

    @Test
    fun getByTypeTest() {
        assertThat(repository.get(type).map { it.getAttribute("key") })
            .hasSize(4)
            .containsAll(listOf("1", "2", "3", "4"))
        repository.close()
        repository = EntityRepository(dbConfig, Level.ERROR)
        assertThat(repository.get(type, includeDeleted = true).map { it.getAttribute("key") })
            .hasSize(4)
            .containsAll(listOf("1", "2", "3", "4"))
        assertThat(repository.get(type, includeDeleted = true))
            .hasSize(4)
    }

    @Test
    fun getById() {
        val ids = documents.getCollection(EntityType::class)
            .withDocumentClass(org.bson.Document::class.java)
            .find()
            .projection(fields(include(Document::_id)))
            .map { it.getObjectId("_id") }
            .toList()
        val entity = repository.get(EntityType::class, ids[0], includeDeleted = true)
        assertThat(entity).isNotNull
        entity as EntityType
        assertThat(entity.key).isEqualTo("1")
        assertThat(entity.value).isEqualTo("value1")
        @Suppress("UsePropertyAccessSyntax")
        assertThat(repository.get(EntityType::class, ids[0], includeDeleted = true)).isNotNull()
        assertThat(repository.get(EntityType::class, ObjectId("111111111111111111111111"))).isNull()
        assertThat(repository.get(EntityType::class, ids[0], includeDeleted = true)).isNotNull
        assertThat(repository.get(EntityType::class, ids[0], includeDeleted = true)).isNotNull
    }

    @Test
    fun getByAttribute() {
        val ids = documents.getCollection(EntityType::class)
            .withDocumentClass(org.bson.Document::class.java)
            .find()
            .projection(fields(include(Document::_id)))
            .map { it.getObjectId("_id") }
            .toList()
        val entity = repository.get(type, mapOf(keyAttr to "2"), includeDeleted = true)
        assertThat(entity).isNotNull
        entity as EntityType
        assertThat(entity._id).isEqualTo(ids[1])
        assertThat(repository.get(type, mapOf(keyAttr to "2"), includeDeleted = true)).isNotNull
        assertThat(repository.get(type, mapOf(keyAttr to "2"), includeDeleted = true)).isNotNull
        assertThat(repository.get(type, mapOf(keyAttr to "5"), includeDeleted = true)).isNull()
        assertThat(repository.get(type, mapOf(keyAttr to "2"), includeDeleted = true)).isNotNull
    }

    @Test
    fun getIDsTest() {
        val ids = documents.getCollection(EntityType::class)
            .withDocumentClass(org.bson.Document::class.java)
            .find()
            .projection(fields(include(Document::_id)))
            .map { it.getObjectId("_id") }
            .toList()
        assertThat(repository.getIDs(type, includeDeleted = true))
            .hasSize(4)
            .containsAll(ids)
    }

    @Test
    fun getPagedIDsTest() {
        val ids = documents.getCollection(EntityType::class)
            .withDocumentClass(org.bson.Document::class.java)
            .find()
            .projection(fields(include(Document::_id)))
            .map { it.getObjectId("_id") }
            .toList()
        assertThat(repository.getIDs(type, 2, 0, includeDeleted = true))
            .hasSize(2)
            .containsExactlyElementsOf(ids.slice(0..1))
        assertThat(repository.getIDs(type, 2, 1, includeDeleted = true))
            .hasSize(2)
            .containsExactlyElementsOf(ids.slice(2..3))
    }

    @Test
    fun markEntitiesAsRemoved() {
        val entities = repository.get(type, includeDeleted = true)
        assertThat(entities.all { !it.removed }).isEqualTo(true)
        entities[1].toucheTime = Documents.timestamp.minusDays(1)
        repository.update(entities[1])
        repository.markEntitiesAsRemoved(type)
        assertThat(repository.get(type, includeDeleted = true)[1].removed).isEqualTo(true)
    }

    @Test
    fun updateAbsentDays() {
        val entities = repository.get(type, includeDeleted = true)
        var entity = entities[1]
        assertThat(entities.all { it.absentDays == 0 }).isEqualTo(true)
        repository.markEntitiesAsRemoved(type)
        entity = repository.get(type, entity._id, true) as EntityType
        entity.toucheTime = Documents.timestamp.minusDays(6)
        repository.update(entity)
        repository.markEntitiesAsRemoved(type)
        entity = repository.get(type, entity._id, true) as EntityType
        entity.removeTime = Documents.timestamp.minusDays(5)
        repository.update(entity)
        repository.updateAbsentDays(type)
        assertThat(repository.get(type, includeDeleted = true)[1].absentDays).isEqualTo(5)
    }

    @Test
    fun removeOldEntities() {
        val entities = repository.get(type, includeDeleted = true)
        var entity = entities[1]
        assertThat(entities.all { it.absentDays == 0 }).isEqualTo(true)
        repository.markEntitiesAsRemoved(type)
        entity.toucheTime = Documents.timestamp.minusDays(6)
        repository.update(entity)
        repository.markEntitiesAsRemoved(type)
        entity = repository.get(type, entity._id, true) as EntityType
        entity.removeTime = Documents.timestamp.minusDays(5)
        repository.update(entity)
        repository.updateAbsentDays(type)
        assertThat(repository.get(type, includeDeleted = true)[1].absentDays).isEqualTo(5)
        repository.removeOldEntities(type, 4)
        assertThat(documents.getCollection(EntityType::class).countDocuments()).isEqualTo(3)
    }
}
