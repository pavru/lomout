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

import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initStringValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DocumentDataTest {
    internal class TestType : Document() {
        var attr: String = initStringValue
        var attr1: String = initStringValue

        companion object : DocumentMetadata(TestType::class)
    }

    @Test
    internal fun test() {
        val data = emptyDocumentData()
        data[TestType::attr] = "test"
        assertThat(data[TestType::attr.toAttribute()]).isEqualTo("test")
        data[TestType::attr.toAttribute()] = "test2"
        assertThat(data[TestType::attr]).isEqualTo("test2")
        assertThat(data.getOrDefault(TestType::attr1, "test3")).isEqualTo("test3")
        assertThat(data.containsKey(TestType::attr)).isTrue()
        assertThat(data.containsKey(TestType::attr1)).isFalse()
    }
}
