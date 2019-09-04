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

import net.pototskiy.apps.lomout.api.CSV_SHEET_NAME
import net.pototskiy.apps.lomout.api.callable.AttributeReader
import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.Reader
import net.pototskiy.apps.lomout.api.callable.ReaderBuilder
import net.pototskiy.apps.lomout.api.callable.createReader
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.document.Key
import net.pototskiy.apps.lomout.api.entity.reader.BooleanListAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.DateAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.DateListAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.DateTimeAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.DateTimeListAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.DocumentAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.DoubleAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.DoubleListAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.LongAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.LongListAttributeReader
import net.pototskiy.apps.lomout.api.entity.reader.StringListAttributeReader
import net.pototskiy.apps.lomout.api.script.script
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import java.time.LocalDate
import java.time.LocalDateTime

class TestReader : AttributeReader<String?>(), ReaderBuilder {
    override operator fun invoke(attribute: Attribute, input: Cell, context: LomoutContext): String? {
        return "test reader function"
    }

    override fun build(): AttributeReader<out Any?> = createReader<TestReader>()
}

class TestEntity1 : Document() {
    @Reader(TestReader::class)
    var stringAttr1: String = ""
    @Key
    var testAttr1: String = ""
    var booleanAttr1: Boolean? = null
    var longAttr1: Long = 0
    var doubleAttr1: Double = 0.0

    companion object : DocumentMetadata(TestEntity1::class)
}

class NestedType : Document() {
    var nested1: Int = 0
    var nested2: Int = 0

    companion object : DocumentMetadata(NestedType::class)
}

class TestEntity2 : Document() {
    var stringListAttr2: List<String> = emptyList()
    var booleanListAttr2: List<Boolean> = emptyList()
    var longListAttr2: List<Long> = emptyList()
    var doubleListAttr2: List<Double> = emptyList()
    var attributeList2: NestedType? = null

    companion object : DocumentMetadata(TestEntity2::class)
}

open class OnecProduct : Document() {
    @Key
    var sku: String = ""
    var description: String = ""
    var bool_val: Boolean = false

    class LongValReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<LongAttributeReader> {
            locale = "ru_RU"
        }
    }

    @Reader(LongValReaderBuilder::class)
    var long_val: Long = 0L

    class DoubleValReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DoubleAttributeReader> {
            locale = "ru_RU"
        }
    }

    @Reader(DoubleValReaderBuilder::class)
    var double_val: Double = 0.0

    class DateValReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DateAttributeReader> {
            pattern = "d.M.uu"
        }
    }

    @Reader(DateValReaderBuilder::class)
    var date_val: LocalDate = LocalDate.MIN

    class DateTimeValReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DateTimeAttributeReader> {
            pattern = "d.M.uu H:m"
        }
    }

    @Reader(DateTimeValReaderBuilder::class)
    var datetime_val: LocalDateTime = LocalDateTime.MIN

    class StringListReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<StringListAttributeReader> {
            delimiter = ';'
            quotes = null
            escape = '\\'
        }
    }

    @Reader(StringListReaderBuilder::class)
    var string_list: List<String> = emptyList()

    class BoolListReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<BooleanListAttributeReader> {
            delimiter = ','
            quotes = null
        }
    }

    @Reader(BoolListReaderBuilder::class)
    var bool_list: List<Boolean> = emptyList()

    class LongListReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<LongListAttributeReader> {
            delimiter = ','
            quotes = null
        }
    }

    @Reader(LongListReaderBuilder::class)
    var long_list: List<Long> = emptyList()

    class DoubleListReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DoubleListAttributeReader> {
            locale = "ru_RU"
            delimiter = ','
            quotes = null
        }
    }

    @Reader(DoubleListReaderBuilder::class)
    var double_list: List<Double> = emptyList()

    class DateListReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DateListAttributeReader> {
            delimiter = ','
            quotes = null
            pattern = "d.M.uu"
        }
    }

    @Reader(DateListReaderBuilder::class)
    var date_list: List<LocalDate> = emptyList()

    class DateTimeListReaserBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DateTimeListAttributeReader> {
            delimiter = ','
            quotes = null
            pattern = "d.M.uu H:m"
        }
    }

    @Reader(DateTimeListReaserBuilder::class)
    var datetime_list: List<LocalDateTime> = emptyList()

    class CompoundReaderBuilder : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
            delimiter = ','
            quotes = null
            valueDelimiter = '='
            valueQuote = '"'
        }
    }

    @Reader(CompoundReaderBuilder::class)
    var compound: NestedType = NestedType()
    var group_code: String? = null
    var group_name: String? = null

    companion object : DocumentMetadata(OnecProduct::class)
}

class ImportProduct : OnecProduct() {
    companion object : DocumentMetadata(ImportProduct::class)
}

script {
    database {
        name("test_db_name")
        server {
            host("remote-host")
            port(3307)
            user("test-user")
            password("test-password")
        }
    }
    loader {
        files {
            val testDataDir = System.getenv("TEST_DATA_DIR")
            file("test-attributes-xls") { path("$testDataDir/test.attributes.xls") }
            file("test-attributes-csv") {
                path("$testDataDir/test.attributes.csv")
                locale("ru_RU")
            }
            file("onec_group") { path("$testDataDir/test-products.xls") }
            file("mage_product") { path("$testDataDir/catalog_product.csv") }
            file("mage_user_group") { path("$testDataDir/customer_group.csv") }
            file("mage_group") { path("$testDataDir/catalog_category.csv") }
            file("mage_customer_group") { path("$testDataDir/customer_group.csv") }
            file("mage_adv_price") { path("$testDataDir/advanced_pricing.csv") }
            file("mage-stock-source") { path("$testDataDir/stock_sources.csv") }
        }
        load<OnecProduct> {
            fromSources { source { file("test-attributes-xls"); sheet("test-stock"); stopOnEmptyRow() } }
            rowsToSkip(3)
            keepAbsentForDays(10)
            sourceFields {
                main("product") {
                    field("sku") { column(0); pattern("^[0-9]+$") }
                    field("description") { column(1) }
                    field("bool_val") { column(2) }
                    field("long_val") { column(3) }
                    field("double_val") { column(4) }
                    field("date_val") { column(5) }
                    field("datetime_val") { column(6) }
                    field("string_list") { column(7) }
                    field("bool_list") { column(8) }
                    field("long_list") { column(9) }
                    field("double_list") { column(10) }
                    field("date_list") { column(11) }
                    field("datetime_list") { column(12) }
                    field("compound") { column(13) }
                }
                extra("group") {
                    field("group_code") { column(0); pattern("^G[0-9]{3,3}$") }
                    field("group_name") { column(1) }
                }
            }
        }
        load<OnecProduct> {
            fromSources { source { file("test-attributes-csv"); sheet(CSV_SHEET_NAME); stopOnEmptyRow() } }
            headersRow(2)
            rowsToSkip(3)
            keepAbsentForDays(10)
            sourceFields {
                main("product") {
                    field("sku") { pattern("^[0-9]+$") }
                    field("description")
                    field("bool_val")
                    field("long_val")
                    field("double_val")
                    field("date_val")
                    field("datetime_val")
                    field("string_list")
                    field("bool_list")
                    field("long_list")
                    field("double_list")
                    field("date_list")
                    field("datetime_list")
                    field("compound")
                }
                extra("group") {
                    field("group_code") { column(0); pattern("^G[0-9]{3,3}$") }
                    field("group_name") { column(1) }
                }
            }
        }
    }
    mediator {
        produce<ImportProduct> {
            input {
                entity(OnecProduct::class)
            }
            pipeline {
                assembler { null }
            }
        }
        produce<ImportProduct> {
            input {
                entity(OnecProduct::class)
            }
            pipeline {
                assembler { null }
            }
        }
    }
}
