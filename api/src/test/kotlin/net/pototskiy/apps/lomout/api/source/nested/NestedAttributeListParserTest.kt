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

package net.pototskiy.apps.lomout.api.source.nested

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NestedAttributeListParserTest {
    @Test
    internal fun parseCorrectDataTest() {
        val parser = NestedAttributeListParser(null, ',', null, '=')
        val v = parser.parse("attr1=value1,attr2=value2")
        assertThat(v).containsAllEntriesOf(
            mapOf(
                "attr1" to "value1",
                "attr2" to "value2"
            )
        )
    }

    @Test
    internal fun parseNoValueDataTest() {
        val parser = NestedAttributeListParser(null, ',', null, '=')
        val v = parser.parse("attr1=,attr2=")
        assertThat(v).containsAllEntriesOf(
            mapOf(
                "attr1" to "",
                "attr2" to ""
            )
        )
    }

    @Test
    internal fun parseNoNameDataTest() {
        val parser = NestedAttributeListParser(null, ',', null, '=')
        val v = parser.parse("=value1,=value2")
        assertThat(v).containsAllEntriesOf(
            mapOf(
                "" to "value1",
                "" to "value2"
            )
        )
    }

    @Test
    internal fun parseNoDataTest() {
        val parser = NestedAttributeListParser(null, ',', null, '=')
        val v = parser.parse("=,=")
        assertThat(v).containsAllEntriesOf(
            mapOf(
                "" to "",
                "" to ""
            )
        )
    }
}
