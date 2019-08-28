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

package net.pototskiy.apps.lomout.api.script.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class LoadBuilderTest {
    @Suppress("unused")
    class EntityType: Document() {
        var f1: String = ""
        var f2: String = ""
        var f3: String = ""
        var f4: String = ""
        companion object :DocumentMetadata(EntityType::class)
    }
    private val helper = ScriptBuildHelper()
    private val entity = EntityType::class

    @Test
    internal fun validateFieldColumnsTest() {
        assertThatThrownBy {
            Load.Builder(helper, entity).apply {
                sourceFields {
                    main("test") {
                        field("f1") { column(0) }
                        field("f2") { column(0) }
                        field("f3") { column(1) }
                        field("f4") { column(1) }
                    }
                }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Field columns '0, 1' are duplicated.")
    }

    @Test
    internal fun fieldColumnsNotDefinedTest() {
        SourceFileCollection.Builder(helper).apply {
            file("file1") { path("test") }
        }.build()
        assertThatThrownBy {
            Load.Builder(helper, entity).apply {
                fromSources { source { file("file1"); sheet("test") } }
                sourceFields {
                    main("test") {
                        field("f1") { }
                        field("f2") { }
                        field("f3") { }
                        field("f4") { }
                    }
                }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Dataset has no headers row but fields 'f1, f2, f3, f4' has no column defined.")
    }

    @Test
    internal fun sourcesNotDefinedTest() {
        assertThatThrownBy {
            Load.Builder(helper, entity).apply {
                sourceFields {
                    main("test") {
                        field("f1") { column(1) }
                        field("f2") { column(2) }
                    }
                }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Source files are not defined for entity type " +
                    "'net.pototskiy.apps.lomout.api.script.loader.LoadBuilderTest.EntityType' loading.")
    }

    @Test
    internal fun sourceFieldsNotDefinedTest() {
        val v = SourceFileCollection.Builder(helper).apply {
            file("file1") { path("test") }
        }.build()
        assertThat(v).isNotNull
        assertThatThrownBy {
            Load.Builder(helper, entity).apply {
                fromSources { source { file("file1"); sheet("test") } }
            }.build()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Field set is not defined for entity type " +
                    "'net.pototskiy.apps.lomout.api.script.loader.LoadBuilderTest.EntityType' loading.")
    }
}
