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

import com.mongodb.ConnectionString
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.PublicApi
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.KMongo
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.fields
import org.litote.kmongo.find
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById
import org.litote.kmongo.include
import org.litote.kmongo.save
import java.time.LocalDateTime
import kotlin.reflect.KClass

/**
 * Document repository
 *
 * @property isClosed
 * @constructor
 */
class Documents(
    databaseName: String,
    host: String = "127.0.0.1",
    port: Int = 27017
) : AutoCloseable {
    private var closed: Boolean = false
    @PublicApi
    val isClosed: Boolean
        get() = closed

    init {
        if (instance != null) {
            throw DocumentException(message("message.error.document.many_documents_instances"))
        }
        timestamp = LocalDateTime.now().let {
            it.withNano(it.nano / MICROSECONDS * MICROSECONDS)
        }
    }

    private val client = KMongo.createClient(
        ConnectionString("mongodb://$host:$port/?maxPoolSize=10")
    )
    private val database: MongoDatabase = client.getDatabase(databaseName)
    private val indexIsOk = mutableMapOf<String, Boolean>()

    /**
     * Insert a new document.
     *
     * @param document The document
     */
    fun insert(document: Document) {
        val collection = getCollection(document)
        collection.insertOne(document)
    }

    /**
     * Update document.
     *
     * @param document The document
     */
    fun update(document: Document) {
        val collection = getCollection(document)
        collection.save(document)
    }

    /**
     * Delete a document.
     *
     * @param document The document
     */
    fun deleteOne(document: Document) {
        val collection = getCollection(document)
        collection.deleteOneById(document._id)
    }

    /**
     * Delete a document by its id.
     *
     * @param type The document type
     * @param id The document id
     */
    fun deleteOne(type: KClass<out Document>, id: ObjectId) {
        val collection = getCollection(type)
        collection.deleteOneById(id)
    }

    /**
     * Delete documents fitted to filter.
     *
     * @param type The document type
     * @param filter The filter
     */
    fun deleteMany(type: KClass<out Document>, filter: Bson) {
        val collection = getCollection(type)
        collection.deleteMany(filter)
    }

    /**
     * Get one document by its id.
     *
     * @param type The document type
     * @param id The document id
     * @return The document
     */
    fun getOne(type: KClass<out Document>, id: ObjectId): Document? {
        val collection = getCollection(type)
        return collection.findOneById(id)
    }

    /**
     * Get one document fitted to the filter.
     *
     * @param type The document type
     * @param filter The filter
     * @return The document
     */
    @Suppress("SpreadOperator")
    fun getOne(type: KClass<out Document>, vararg filter: Bson): Document? {
        val collection = getCollection(type)
        return collection.findOne(*filter)
    }

    /**
     * Get documents by a filter.
     *
     * @param type The document type
     * @param filter The filter
     * @return Documents list
     */
    @Suppress("SpreadOperator")
    fun getMany(type: KClass<out Document>, vararg filter: Bson): List<Document> {
        val collection = getCollection(type)
        return collection.find(*filter).toList()
    }

    /**
     * Get all documents of given type.
     *
     * @param type The document type
     * @return Documents list
     */
    fun getMany(type: KClass<out Document>): List<Document> {
        val collection = getCollection(type)
        return collection.find().toList()
    }

    /**
     * Get document ids of given document type.
     *
     * @param type The document type
     * @return The list of ids
     */
    fun getManyID(type: KClass<out Document>): List<ObjectId> {
        val collection = getCollection(type)
        return collection.withDocumentClass(org.bson.Document::class.java).find()
            .projection(fields(include(Document::_id)))
            .map { it.getObjectId("_id") }
            .toList()
    }

    /**
     * Get document ids for given type and filter.
     *
     * @param type The document type
     * @param filter The filter
     * @return The list of id
     */
    @Suppress("SpreadOperator")
    fun getManyID(type: KClass<out Document>, vararg filter: Bson): List<ObjectId> {
        val collection = getCollection(type)
        return collection.withDocumentClass(org.bson.Document::class.java).find(*filter)
            .projection(fields(include(Document::_id)))
            .map { it.getObjectId("_id") }
            .toList()
    }

    /**
     * Get all document ids. The paged version.
     *
     * @param type The document type
     * @param pageSize The page size
     * @param pageNumber The page's number. Zero based
     * @return The list of id
     */
    fun getManyID(type: KClass<out Document>, pageSize: Int, pageNumber: Int): List<ObjectId> {
        val collection = getCollection(type)
        return collection.withDocumentClass(org.bson.Document::class.java).find()
            .projection(fields(include(Document::_id)))
            .skip(pageNumber * pageSize)
            .limit(pageSize)
            .map { it.getObjectId("_id") }
            .toList()
    }

    /**
     * Get document ids for given type and filter. The paged version.
     *
     * @param type The document type
     * @param pageSize The page size
     * @param pageNumber The page's number. Zero based
     * @param filter The filter
     * @return The list of id
     */
    @Suppress("SpreadOperator")
    fun getManyID(type: KClass<out Document>, pageSize: Int, pageNumber: Int, vararg filter: Bson): List<ObjectId> {
        val collection = getCollection(type)
        return collection.withDocumentClass(org.bson.Document::class.java).find(*filter)
            .projection(fields(include(Document::_id)))
            .skip(pageNumber * pageSize)
            .limit(pageSize)
            .map { it.getObjectId("_id") }
            .toList()
    }

    private fun checkCollectionIndex(collection: MongoCollection<out Document>, metadata: DocumentMetadata) {
        if (indexIsOk[metadata.collectionName] == true) return
        val currentIndex = collection.listIndexes().toList().filter { it.getString("name") != "_id_" }
        val indexToRemove = currentIndex.filter { index ->
            index.getString("name") !in metadata.indexes.map { it.name } ||
                    !index.get("key", org.bson.Document::class.java).map { it.key }.containsAll(
                        metadata.indexes.find {
                            it.name == index.getString("name")
                        }?.keys?.map { it.name }
                            ?: emptyList()
                    )
        }.map { it.getString("name") }
        val indexToCreate =
            metadata.indexes.filter { index -> index.name !in currentIndex.map { it.getString("name") } }
        indexToRemove.forEach { collection.dropIndex(it) }
        indexToCreate.forEach { index ->
            val options = IndexOptions()
                .name(index.name)
                .unique(index.isUnique)
                .background(true)
            val keys = createIndexKeys(index)
            collection.createIndex(keys, options)
        }
        indexIsOk[metadata.collectionName] = true
    }

    private fun createIndexKeys(index: DocumentMetadata.JsonIndex): Bson {
        return if (index.keys.size == 1) {
            when (index.keys.first().sortOrder) {
                Index.SortOrder.ASC -> Indexes.ascending(index.keys.first().name)
                Index.SortOrder.DESC -> Indexes.descending(index.keys.first().name)
            }
        } else {
            @Suppress("SpreadOperator")
            Indexes.compoundIndex(*index.keys.map {
                when (it.sortOrder) {
                    Index.SortOrder.ASC -> Indexes.ascending(it.name)
                    Index.SortOrder.DESC -> Indexes.descending(it.name)
                }
            }.toTypedArray())
        }
    }

    /**
     * Get document collection.
     *
     * @param document The document
     * @return The mongo collection
     */
    @Suppress("UNCHECKED_CAST")
    fun getCollection(document: Document): MongoCollection<Document> =
        database.getCollection(
            document.documentMetadata.collectionName,
            document::class.java as Class<Document>
        ).also {
            checkCollectionIndex(it, document.documentMetadata)
        }

    /**
     * Get document collection.
     *
     * @param documentType The document type
     * @return The mongo collection
     */
    @Suppress("UNCHECKED_CAST")
    fun getCollection(documentType: KClass<out Document>): MongoCollection<Document> =
        database.getCollection(
            documentType.documentMetadata.collectionName,
            documentType.java as Class<Document>
        ).also {
            checkCollectionIndex(it, documentType.documentMetadata)
        }

    /**
     * Close repository
     */
    override fun close() {
        closed = true
        client.close()
        instance = null
    }

    companion object {
        private const val MICROSECONDS = 1000000
        private var instance: Documents? = null
        /**
         * Timestamp for the current session.
         */
        var timestamp: LocalDateTime = LocalDateTime.now().let {
            it.withNano(it.nano / MICROSECONDS * MICROSECONDS)
        }
    }
}
