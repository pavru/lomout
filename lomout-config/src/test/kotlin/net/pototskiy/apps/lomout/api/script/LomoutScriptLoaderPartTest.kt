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

package net.pototskiy.apps.lomout.api.script

import net.pototskiy.apps.lomout.api.NoExitSecurityManager
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.createLocale
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

@Suppress("MagicNumber")
@DisplayName("Create config with loader section")
internal class LomoutScriptLoaderPartTest {
    private lateinit var lomoutScript: LomoutScript
    @BeforeEach
    internal fun setUp() {
        System.setSecurityManager(NoExitSecurityManager())
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        lomoutScript = ScriptBuilderFromDSL(
            File("${System.getenv("TEST_DATA_DIR")}/conf-test.lomout.kts")
        ).lomoutScript
    }

    @Test
    internal fun databaseConfigurationTest() {
        assertThat(lomoutScript.database.name).isEqualTo("test_db_name")
        assertThat(lomoutScript.database.server.host).isEqualTo("remote-host")
        assertThat(lomoutScript.database.server.port).isEqualTo(3307)
        assertThat(lomoutScript.database.server.user).isEqualTo("test-user")
        assertThat(lomoutScript.database.server.password).isEqualTo("test-password")
    }

    @Test
    internal fun loaderFilesConfigurationTest() {
        assertThat(lomoutScript.loader?.files).hasSize(9)
        assertThat(lomoutScript.loader?.files?.map { it.file }).containsExactlyElementsOf(files)
        assertThat(lomoutScript.loader?.files?.filter { it.locale == "ru_RU".createLocale() }).hasSize(1)
        assertThat(lomoutScript.loader?.files?.filter { it.locale != "ru_RU".createLocale() }).hasSize(8)
    }

    companion object {
        private val files = listOf(
            File("${System.getenv("TEST_DATA_DIR")}/test.attributes.xls"),
            File("${System.getenv("TEST_DATA_DIR")}/test.attributes.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/test-products.xls"),
            File("${System.getenv("TEST_DATA_DIR")}/catalog_product.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/customer_group.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/catalog_category.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/customer_group.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/advanced_pricing.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/stock_sources.csv")
        )
    }
}
