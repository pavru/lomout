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

package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.config.Config
import net.pototskiy.apps.lomout.api.config.ConfigurationBuilderFromDSL
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File

internal class LoadingDataTestPrepare {
    private lateinit var config: Config

    init {
//        BasicConfigurator.configure()
    }

    @Suppress("unused")
    internal fun loadConfiguration(): Config {
        config = ConfigurationBuilderFromDSL(
            File(this::class.java.classLoader.getResource("test.config.kts").toURI())
        ).config
        return config
    }

    internal fun loadConfiguration(file: String): Config {
        config = ConfigurationBuilderFromDSL(File(file)).config
        return config
    }

    @Suppress("unused")
    internal fun openHSSWorkbookFromResources(name: String): HSSFWorkbook {
        val testData = this::class.java.classLoader.getResourceAsStream(name)
        return HSSFWorkbook(testData)
    }
}
