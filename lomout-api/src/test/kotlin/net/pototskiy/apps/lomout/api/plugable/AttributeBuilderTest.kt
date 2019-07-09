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

package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initLongValue
import org.assertj.core.api.Assertions.assertThat
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class AttributeBuilderTest {
    class TestDoc : Document() {
        var attr1: Long = initLongValue
        @get: BsonIgnore
        val attr2: Long by lazy { attr2Builder.build(this) }
        @get:BsonIgnore
        val attr3: Long
            get() = attr2Builder.build(this)

        companion object : DocumentMetadata(TestDoc::class) {
            val attr2Builder = TestBuilder()
        }
    }

    class TestBuilder : AttributeBuilder<Long>() {
        /**
         * Builder function
         *
         * @param entity DbEntity The entity to build value
         * @return R? The value type to return
         */
        override fun build(entity: Document): Long {
            return (entity.getAttribute("attr1") as Long) * 3L
        }
    }

    @Test
    internal fun justTest() {
        val doc = TestDoc().apply { attr1 = 3L }
        assertThat(doc.attr1).isEqualTo(3L)
        assertThat(doc.attr2).isEqualTo(9L)
        assertThat(doc.attr3).isEqualTo(9L)
        doc.attr1 = 4L
        assertThat(doc.attr1).isEqualTo(4L)
        assertThat(doc.attr2).isEqualTo(9L)
        assertThat(doc.attr3).isEqualTo(12L)
    }
}
