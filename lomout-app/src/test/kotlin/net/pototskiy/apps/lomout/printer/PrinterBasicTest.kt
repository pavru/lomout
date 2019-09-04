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

package net.pototskiy.apps.lomout.printer

import net.pototskiy.apps.lomout.LogCatcher
import net.pototskiy.apps.lomout.api.PRINTER_LOG_NAME
import net.pototskiy.apps.lomout.api.ROOT_LOG_NAME
import net.pototskiy.apps.lomout.api.callable.AttributeWriter
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.Writer
import net.pototskiy.apps.lomout.api.callable.WriterBuilder
import net.pototskiy.apps.lomout.api.createContext
import net.pototskiy.apps.lomout.api.callable.createWriter
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.Key
import net.pototskiy.apps.lomout.api.entity.EntityRepository
import net.pototskiy.apps.lomout.api.script.LomoutScript
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.script.mediator.Pipeline
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.WorkbookFactory
import net.pototskiy.apps.lomout.loader.DataLoader
import net.pototskiy.apps.lomout.mediator.DataMediator
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock
import java.io.File

@ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
@Suppress("ComplexMethod", "MagicNumber")
internal class PrinterBasicTest {
    private val helper = ScriptBuildHelper()
    private val testDataDir = System.getenv("TEST_DATA_DIR")
    private val fileName = "$testDataDir/mediator-test-data.xls"
    private val outputName = "../tmp/printer-basic-test.xls"

    @ResourceLock(value = "DB", mode = ResourceAccessMode.READ_WRITE)
    @Test
    internal fun printerBasicTest() {
        File("../tmp/$outputName").parentFile.mkdirs()
        val script = createConfiguration()

        System.setProperty("mediation.line.cache.size", "4")
        System.setProperty("printer.line.cache.size", "4")
        val repository = EntityRepository(script.database, Level.ERROR)
        LomoutContext.setContext(createContext {
            this.script = script
            scriptFile = File("no-file.lomout.kts")
            this.repository = repository
            logger = LogManager.getLogger(PRINTER_LOG_NAME)
        })
        repository.getIDs(PrinterBasicEntity1::class).forEach { repository.delete(PrinterBasicEntity1::class, it) }
        repository.getIDs(PrinterBasicEntity2::class).forEach { repository.delete(PrinterBasicEntity2::class, it) }
        repository.getIDs(PrinterBasicImportData::class).forEach { repository.delete(PrinterBasicImportData::class, it) }
        DataLoader().load()
        DataMediator().mediate()
        Configurator.setLevel(ROOT_LOG_NAME, Level.TRACE)
        val catcher = LogCatcher()
        catcher.startToCatch(Level.OFF, Level.ERROR)
        DataPrinter().print()
        val log = catcher.log
        catcher.stopToCatch()
        assertThat(log).doesNotContain("[ERROR]")
        @Suppress("UNCHECKED_CAST")
        val entities = repository.get(PrinterBasicImportData::class) as List<PrinterBasicImportData>
        WorkbookFactory.create(File(outputName).toURI().toURL()).use { workbook ->
            val sheet = workbook["test"]
            assertThat(sheet).isNotNull
            val headRow = sheet[0]
            assertThat(headRow).isNotNull
            assertThat(headRow!![0]?.stringValue).isEqualTo("sku")
            assertThat(headRow[1]?.stringValue).isEqualTo("desc")
            assertThat(headRow[2]?.stringValue).isEqualTo("corrected_amount")
            for (i in 1..7 step 2) {
                val sku = sheet[i + 1]!![0]?.longValue
                val entity = entities.findLast { it.sku == sku }!!
                @Suppress("UsePropertyAccessSyntax")
                assertThat(sheet[i]!![0]?.doubleValue).isNotNull().isEqualTo(entity.amount)
                @Suppress("UsePropertyAccessSyntax")
                assertThat(sheet[i + 1]!![1]?.stringValue).isNotNull().isEqualTo(entity.desc)
                @Suppress("UsePropertyAccessSyntax")
                assertThat(sheet[i + 1]!![2]?.doubleValue).isNotNull().isEqualTo(entity.corrected_amount)
            }
        }
        repository.close()
    }

    internal class SimpleLongWriter : AttributeWriter<Long?>(), WriterBuilder {
        /**
         * Writer function
         *
         * @param value T? The value to write
         * @param cell Cell The cell to write value
         */
        override operator fun invoke(value: Long?, cell: Cell, context: LomoutContext) {
            cell.setCellValue(value!!)
        }

        override fun build(): AttributeWriter<out Any?> = createWriter<SimpleLongWriter>()
    }

    internal class SimpleDoubleWriter : AttributeWriter<Double?>(), WriterBuilder {
        /**
         * Writer function
         *
         * @param value T? The value to write
         * @param cell Cell The cell to write value
         */
        override operator fun invoke(value: Double?, cell: Cell, context: LomoutContext) {
            cell.setCellValue(value!!)
        }

        override fun build(): AttributeWriter<out Any?> = createWriter<SimpleDoubleWriter>()
    }

    @Suppress("PropertyName")
    internal open class PrinterBasicEntity1 : Document() {
        @Key
        @Writer(SimpleLongWriter::class)
        var sku: Long = 0L
        var desc: String = ""
        @Writer(SimpleDoubleWriter::class)
        var amount: Double = 0.0
        @Suppress("VariableNaming")
        open val corrected_amount: Double
            get() = amount * 11.0

        companion object : DocumentMetadata(PrinterBasicEntity1::class)
    }

    internal class PrinterBasicEntity2 : PrinterBasicEntity1() {
        override val corrected_amount: Double
            get() = amount * 13.0

        companion object : DocumentMetadata(PrinterBasicEntity2::class)
    }

    internal class PrinterBasicImportData : PrinterBasicEntity1() {
        @Writer(SimpleDoubleWriter::class)
        override var corrected_amount: Double = 0.0

        companion object : DocumentMetadata(PrinterBasicImportData::class)
    }

    @Suppress("LongMethod")
    private fun createConfiguration() = LomoutScript.Builder(helper).apply {
        database {
            name("lomout_test")
            server {
                host("localhost")
                port(27017)
                user("root")
                password(if (System.getenv("TRAVIS_BUILD_DIR") == null) "root" else "")
            }
        }
        loader {
            files {
                file("test-data") { path(fileName) }
            }
            load<PrinterBasicEntity1> {
                fromSources { source { file("test-data"); sheet("entity1"); stopOnEmptyRow() } }
                rowsToSkip(1)
                keepAbsentForDays(1)
                sourceFields {
                    main("entity") {
                        field("sku") { column(0) }
                        field("desc") { column(1) }
                        field("amount") { column(2) }
                    }
                }
            }
            load<PrinterBasicEntity2> {
                fromSources { source { file("test-data"); sheet("entity2"); stopOnEmptyRow() } }
                rowsToSkip(1)
                keepAbsentForDays(1)
                sourceFields {
                    main("entity") {
                        field("sku") { column(0) }
                        field("desc") { column(1) }
                        field("amount") { column(2) }
                    }
                }
            }
        }
        mediator {
            produce<PrinterBasicImportData> {
                input {
                    entity(PrinterBasicEntity1::class)
                    entity(PrinterBasicEntity2::class)
                }
                pipeline {
                    classifier { element ->
                        var entity = element.entities.getOrNull(PrinterBasicEntity1::class)
                        if (entity != null) {
                            val partnerType = PrinterBasicEntity2::class
                            val partner = repository.get(
                                partnerType,
                                mapOf(PrinterBasicEntity2.attributes.getValue("sku") to entity.getAttribute("sku")!!)
                            )
                            if (partner != null) {
                                element.match(partner)
                            } else {
                                element.mismatch()
                            }
                        } else if (element.entities.getOrNull(PrinterBasicEntity2::class) != null) {
                            entity = element.entities[PrinterBasicEntity2::class]
                            val partnerType = PrinterBasicEntity1::class
                            val partner = repository.get(
                                partnerType,
                                mapOf(PrinterBasicEntity1.attributes.getValue("sku") to entity.getAttribute("sku")!!)
                            )
                            if (partner != null) {
                                element.skip()
                            } else {
                                element.mismatch()
                            }
                        } else {
                            element.skip()
                        }
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler { entities ->
                            val e1 = entities[0] as PrinterBasicEntity1
                            val ip = PrinterBasicImportData()
                            ip.sku = e1.sku
                            ip.desc = e1.desc
                            ip.amount = e1.amount
                            ip.corrected_amount = (entities[0] as PrinterBasicEntity1).corrected_amount
                            ip
                        }
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier {
                            val entities = it.entities
                            if (entities[0].documentMetadata.klass == PrinterBasicEntity2::class) {
                                it.match()
                            } else {
                                it.mismatch()
                            }
                        }
                        assembler { entities ->
                            val ip = PrinterBasicImportData()
                            val e2 = entities[0] as PrinterBasicEntity2
                            ip.sku = e2.sku
                            ip.desc = e2.desc
                            ip.amount = e2.amount
                            ip.corrected_amount = (entities[0] as PrinterBasicEntity2).corrected_amount

                            ip
                        }
                    }
                }
            }
        }
        printer {
            files {
                file("output") { path(outputName) }
            }
            print<PrinterBasicImportData> {
                input {
                    entity(PrinterBasicImportData::class)
                }
                output {
                    file { file("output"); sheet("test") }
                    printHead = true
                    outputFields {
                        main("main-data") {
                            field("sku") { column(0) }
                            field("desc") { column(1) }
                            field("corrected_amount") { column(2) }
                        }
                        extra("extra-data") {
                            field("amount") { column(0) }
                        }
                    }
                }
                pipeline {
                    classifier { it.match() }
                    assembler { it.first() as PrinterBasicImportData }
                }
            }
        }
    }.build()
}
