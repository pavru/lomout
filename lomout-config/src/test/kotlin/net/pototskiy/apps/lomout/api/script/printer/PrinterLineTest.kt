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

package net.pototskiy.apps.lomout.api.script.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.script.loader.SourceFileCollection
import net.pototskiy.apps.lomout.api.script.mediator.Pipeline
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class PrinterLineTest {
    @Suppress("PropertyName", "unused")
    class ImportCategory : Document() {
        var attr1: String = ""
        var entity_id: String = ""

        companion object : DocumentMetadata(ImportCategory::class)
    }

    private val helper = ScriptBuildHelper().also { helper ->
        helper.pushScope("printer")
        SourceFileCollection.Builder(helper).apply {
            file("mage-category") { path("no-path") }
        }.build()
    }

    @Test
    internal fun noPipelineAssemblerTest() {
        assertThatThrownBy {
            createPrinterLine()
        }.isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Pipeline with the matched child must have assembler")
    }

    private fun createPrinterLine(): PrinterLine<*> = PrinterLine.Builder(
        helper,
        ImportCategory::class
    ).apply {
        input {
            entity(ImportCategory::class)
        }
        output {
            file { file("mage-category"); sheet("default") }
            printHead = true
            outputFields {
                main("category") {
                    field("entity_id")
                }
            }
        }
        pipeline {
            classifier {
                it.match()
            }
            pipeline(Pipeline.CLASS.MATCHED) {

            }
            pipeline(Pipeline.CLASS.UNMATCHED) {

            }
        }
    }.build()
}
