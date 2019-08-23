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

import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.script.ScriptBuilderFromDSL
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File

internal class LoadingDataTestPrepare {
    private lateinit var lomoutScript: LomoutScript

    @Suppress("unused")
    internal fun loadConfiguration(): LomoutScript {
        lomoutScript = ScriptBuilderFromDSL(
            File(this::class.java.classLoader.getResource("test.config.kts").toURI())
        ).lomoutScript
        return lomoutScript
    }

    internal fun loadConfiguration(file: String): LomoutScript {
        lomoutScript = ScriptBuilderFromDSL(File(file)).lomoutScript
        return lomoutScript
    }

    @Suppress("unused")
    internal fun openHSSWorkbookFromResources(name: String): HSSFWorkbook {
        val testData = this::class.java.classLoader.getResourceAsStream(name)
        return HSSFWorkbook(testData)
    }
}
