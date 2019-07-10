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

import org.assertj.core.api.Assertions.assertThat
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.junit.jupiter.api.Assertions.assertTimeout
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import java.time.Duration

@Suppress("MagicNumber")
@DisplayName("create")
@Disabled
internal class PoCPerformanceTest {

    @Test
    internal fun createTimeTest() {
        assertTimeout(Duration.ofSeconds(40L)) {
            val docs = Documents("lomout_test")
            val col = docs.getCollection(Product::class)
            col.deleteMany(org.bson.Document())
            for (i in 1..100000) {
                val product = Product(
                    i.toString(),
                    listOf((i + 1).toString(), (i + 2).toString())
                )
                docs.insert(product)
            }
            assertThat(col.countDocuments()).isEqualTo(100000)
            docs.close()
        }
    }

    @Nested
    @DisplayName("select")
    internal inner class SelectSpeedTest {
        @Test
        internal fun selectTimeTest() {
            assertTimeout(Duration.ofSeconds(40L)) {
                val docs = Documents("lomout_test")
                val col = docs.getCollection(Product::class)
                for (i in 1..100000) {
                    val product = docs.getOne(Product::class, Product::sku eq i.toString())
                    assertThat(product).isNotNull
                }
                assertThat(col.countDocuments()).isEqualTo(100000)
                docs.close()
            }
        }

        @Nested
        @DisplayName("deleteOne")
        internal inner class DeleteSpeedTest {
            @Test
            internal fun deleteTimeTest() {
                assertTimeout(Duration.ofSeconds(40L)) {
                    val docs = Documents("lomout_test")
                    val col = docs.getCollection(Product::class)
                    for (i in 1..100000) {
                        val result = col.deleteOne(Product::sku eq i.toString())
                        @Suppress("UsePropertyAccessSyntax")
                        assertThat(result).isNotNull()
                        assertThat(result.deletedCount).isEqualTo(1)
                    }
                    assertThat(col.countDocuments()).isEqualTo(0)
                    docs.close()
                }
            }
        }
    }

    @Suppress("unused")
    class AddOptions : Document() {
        var option1: String = "opt1"
        var option2: Long = 101L

        companion object : DocumentMetadata(AddOptions::class)
    }

    @Suppress("unused")
    class Product(
        sku: String,
        categories: List<String>
    ) : Document() {
        @Suppress("CanBePrimaryConstructorProperty")
        @Key
        @Index("index_sku_name", Index.SortOrder.ASC)
        var sku: String = sku
        @Suppress("CanBePrimaryConstructorProperty")
        var categories: List<String> = categories
        var test: Long? = null
        var addOptions: AddOptions =
            AddOptions()
        @Index("index_sku_name", Index.SortOrder.DESC)
        var name: String = ""
        @BsonIgnore
        var testIgnore = ""

        companion object : DocumentMetadata(Product::class)
    }
}
