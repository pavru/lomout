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

package net.pototskiy.apps.lomout.api.source

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

@Suppress("MagicNumber")
internal class HeaderReaderKtTest {
    @Suppress("unused")
    internal class EntityType : Document() {
        var h1: String = ""
        var h2: String = ""
        var h3: String = ""
        var h4: String = ""

        companion object : DocumentMetadata(EntityType::class)
    }

    private val helper = ConfigBuildHelper()

    @Test
    internal fun createCorrectConfigurationTest() {
        createConfiguration()
        assertThat(EntityType.attributes).hasSize(4)
        assertThat(EntityType.attributes.values.map { it.name })
            .containsExactlyElementsOf(listOf("h1", "h2", "h3", "h4"))
    }

    @Test
    internal fun differentHeadersNumberTest() {
        assertThatThrownBy { createConfigurationDifferentHeadersNumber() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Sources have different number of fields")
    }

    @Test
    internal fun differentHeadersOrderTest() {
        assertThatThrownBy { createConfigurationDifferentHeadersOrder() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Sources have different fields or fields in different columns")
    }

    private fun createConfiguration() = Config.Builder(helper).apply {
        database {
            name("test_lomout")
            server {
                host("localhost")
                port(3306)
                user("root")
                password(if (System.getenv("TRAVIS_BUILD_DIR") != null) "" else "root")
            }
        }
        loader {
            files {
                val testDataDir = System.getenv("TEST_DATA_DIR") ?: "../testdata"
                file("test-data") { path("$testDataDir/headers-from-source-test.xls") }
            }
            loadEntity(EntityType::class) {
                headersRow(0)
                fromSources {
                    source { file("test-data"); sheet("Sheet1"); }
                    source { file("test-data"); sheet("Sheet2"); }
                }
                sourceFields {
                    main("main") {
                        field("h1")
                    }
                }
            }
        }
    }.build()

    private fun createConfigurationDifferentHeadersNumber() = Config.Builder(helper).apply {
        database {
            name("test_lomout")
            server {
                host("localhost")
                port(3306)
                user("root")
                password(if (System.getenv("TRAVIS_BUILD_DIR") != null) "" else "root")
            }
        }
        loader {
            files {
                val testDataDir = System.getenv("TEST_DATA_DIR") ?: "../testdata"
                file("test-data") { path("$testDataDir/headers-from-source-test.xls") }
            }
            loadEntity(EntityType::class) {
                headersRow(0)
                fromSources {
                    source { file("test-data"); sheet("Sheet1"); }
                    source { file("test-data"); sheet("Sheet3"); }
                }
                sourceFields {
                    main("main") {
                        field("h1")
                    }
                }
            }
        }
    }.build()

    private fun createConfigurationDifferentHeadersOrder() = Config.Builder(helper).apply {
        database {
            name("test_lomout")
            server {
                host("localhost")
                port(3306)
                user("root")
                password(if (System.getenv("TRAVIS_BUILD_DIR") != null) "" else "root")
            }
        }
        loader {
            files {
                val testDataDir = System.getenv("TEST_DATA_DIR") ?: "../testdata"
                file("test-data") { path("$testDataDir/headers-from-source-test.xls") }
            }
            loadEntity(EntityType::class) {
                headersRow(0)
                fromSources {
                    source { file("test-data"); sheet("Sheet1"); }
                    source { file("test-data"); sheet("Sheet4"); }
                }
                sourceFields {
                    main("main") {
                        field("h1")
                    }
                }
            }
        }
    }.build()
}
