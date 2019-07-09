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

package net.pototskiy.apps.lomout.api.document

import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDateValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initStringValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@Suppress("MagicNumber")
internal class DocumentIndexTest {
    private lateinit var documents: Documents

    @BeforeEach
    internal fun beforeEach() {
        documents = Documents("lomout_test")
        val collection = documents.getCollection(TestDoc::class)
        collection.deleteMany(org.bson.Document())
        val indexes = documents.getCollection(TestDoc::class).listIndexes().map { it.getString("name") }
        TestDoc::class.documentMetadata.indexes.forEach { index ->
            index.takeIf { it.name in indexes }?.let { collection.dropIndex(it.name) }
        }
        documents.close()
        documents = Documents("lomout_test")
    }

    @AfterEach
    internal fun afterEach() {
        documents.close()
    }

    @Test
    internal fun indexCreatingTest() {
        assertThat(TestDoc::class.documentMetadata.indexes).hasSize(4)
        assertThat(TestDoc::class.documentMetadata.indexes.map { it.name })
            .containsAll(listOf("key_index", "index_longAttr", "index_longAttr_doubleAttr", "index_dupDouble"))
        assertThat(TestDoc::class.documentMetadata.indexes.find { it.name == "key_index" }!!.keys.map { it.name })
            .containsAll(listOf("stringAttr", "dateAttr"))
        assertThat(TestDoc::class.documentMetadata.indexes.find { it.name == "index_longAttr" }!!.keys.map { it.name })
            .containsAll(listOf("longAttr"))
        assertThat(TestDoc::class.documentMetadata.indexes.find { it.name == "index_longAttr_doubleAttr" }!!.keys.map { it.name })
            .containsAll(listOf("longAttr", "doubleAttr"))
        assertThat(TestDoc::class.documentMetadata.indexes.find { it.name == "index_dupDouble" }!!.keys.map { it.name })
            .containsAll(listOf("dupDouble"))
        @Suppress("UNUSED_VARIABLE")
        val collection = documents.getCollection(TestDoc::class)
        val createdIndexes = documents.getCollection(TestDoc::class).listIndexes().toList()
        assertThat(createdIndexes.map { it.getString("name") }).hasSize(5)
            .containsAll(listOf("_id_", "key_index", "index_longAttr", "index_longAttr_doubleAttr", "index_dupDouble"))
        assertThat(createdIndexes.find { it.getString("name") == "key_index" }!!
            .get("key", org.bson.Document::class.java).map { it.key })
            .containsAll(listOf("stringAttr", "dateAttr"))
        assertThat(createdIndexes.find { it.getString("name") == "index_longAttr_doubleAttr" }!!
            .get("key", org.bson.Document::class.java).map { it.key })
            .containsAll(listOf("longAttr", "doubleAttr"))
        assertThat(createdIndexes.find { it.getString("name") == "index_longAttr" }!!
            .get("key", org.bson.Document::class.java).map { it.key })
            .containsAll(listOf("longAttr"))
        assertThat(createdIndexes.find { it.getString("name") == "index_dupDouble" }!!
            .get("key", org.bson.Document::class.java).map { it.key })
            .containsAll(listOf("dupDouble"))
    }

    @Test
    internal fun openClassTest() {
        val doc = DocTypeOne().apply {
            attr1 = "test-value-1"
        }
        documents.insert(doc)
        val loaded = documents.getOne(DocTypeOne::class, doc._id) as DocTypeOne
        assertThat(loaded.attr1).isEqualTo("test-value-1")
    }

    @Suppress("unused")
    data class TestDoc(
        var testId: String,
        @Key
        var stringAttr: String,
        @Indexes(
            [
                Index("index_longAttr"),
                Index("index_longAttr_doubleAttr")
            ]
        )
        var longAttr: Long,
        @Index("index_longAttr_doubleAttr", Index.SortOrder.DESC)
        var doubleAttr: Double? = null,
        @Index("index_dupDouble", Index.SortOrder.DESC)
        var dupDouble: Double? = null,
        @Key
        var dateAttr: LocalDate = initDateValue
    ) : Document() {
        @Transient
        var notAnAttribute: Boolean = true

        companion object : DocumentMetadata(TestDoc::class)
    }

    open class DocTypeOne : Document() {
        var attr1: String = initStringValue

        companion object : DocumentMetadata(DocTypeOne::class)
    }

    @Suppress("unused")
    class DocTypeTwo : DocTypeOne() {
        var attr2: String = initStringValue

        companion object : DocumentMetadata(DocTypeTwo::class)
    }
}
