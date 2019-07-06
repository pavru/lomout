class TestReader : AttributeReader<String?>(), ReaderBuilder {
    override fun read(attribute: Attribute, input: Cell): String? {
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
    class StringListReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<StringListAttributeReader> {
            delimiter = ';'
            quote = null
        }
    }
    @Reader(StringListReaderBuilder::class)
    var string_list: List<String> = emptyList()
    class BoolListReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<BooleanListAttributeReader> {
            delimiter = ','
            quote = null
        }
    }
    @Reader(BoolListReaderBuilder::class)
    var bool_list: List<Boolean> = emptyList()
    class LongListReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<LongListAttributeReader>{
            delimiter = ','
            quote = null
        }
    }
    @Reader(LongListReaderBuilder::class)
    var long_list: List<Long> = emptyList()
    class DoubleListReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DoubleListAttributeReader> {
            locale = "ru_RU"
            delimiter = ','
            quote = null
        }
    }
    @Reader(DoubleListReaderBuilder::class)
    var double_list: List<Double> = emptyList()
    class DateListReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DateListAttributeReader>{
            delimiter = ','
            quote = null
            pattern = "d.M.uu"
        }
    }
    @Reader(DateListReaderBuilder::class)
    var date_list: List<LocalDate> = emptyList()
    class DateTimeListReaserBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DateTimeListAttributeReader> {
            delimiter = ','
            quote = null
            pattern = "d.M.uu H:m"
        }
    }
    @Reader(DateTimeListReaserBuilder::class)
    var datetime_list: List<LocalDateTime> = emptyList()
    class CompoundReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
            delimiter = ','
            quote = null
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

config {
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
        loadEntity(OnecProduct::class) {
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
        loadEntity(OnecProduct::class) {
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
        productionLine {
            input {
                entity(OnecProduct::class)
            }
            output(ImportProduct::class)
            pipeline {
                assembler { _, _ -> emptyMap() }
            }
        }
        productionLine {
            input {
                entity(OnecProduct::class)
            }
            output(ImportProduct::class)
            pipeline {
                assembler { _, _ -> emptyMap() }
            }
        }
    }
}
