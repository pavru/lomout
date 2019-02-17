config {
    database {
        name("test_magemediation")
        server {
            host("localhost")
            port(3306)
            user("root")
            password("root")
        }
    }
    loader {
        files {
            val testDataDir = System.getenv("TEST_DATA_DIR")
            file("test-attributes-xls") { path("$testDataDir/test.attributes.xls") }
            file("test-attributes-csv") { path("$testDataDir/test.attributes.csv") }
            file("onec_group") { path("$testDataDir/test-products.xlsx") }
            file("mage_product") { path("$testDataDir/catalog_product.csv") }
            file("mage_user_group") { path("$testDataDir/customer_group.csv") }
            file("mage_group") { path("$testDataDir/catalog_category.csv") }
            file("mage_customer_group") { path("$testDataDir/customer_group.csv") }
            file("mage_adv_price") { path("$testDataDir/advanced_pricing.csv") }
            file("mage-stock-source") { path("$testDataDir/stock_sources.csv") }
        }
        entities {
            entity("onec-product") {
                attribute("sku") { type { string() }.key() }
                attribute("description") { type { text() } }
                attribute("bool_val") { type { bool() } }
                attribute("long_val") { type { long() } }
                attribute("double_val") { type { double().locale("ru_RU") } }
                attribute("date_val") { type { date().pattern("d.M.yy") } }
                attribute("datetime_val") { type { datetime().pattern("d.M.yy H:m") } }
                attribute("string_list") { type { stringList().delimiter(",").quote("") } }
                attribute("bool_list") { type { boolList().delimiter(",").quote("") } }
                attribute("long_list") { type { longList().delimiter(",").quote("") } }
                attribute("double_list") { type { doubleList().locale("ru_RU").delimiter("|").quote("") } }
                attribute("date_list") { type { dateList().delimiter(",").quote("").pattern("d.M.yy") } }
                attribute("datetime_list") { type { datetimeList().delimiter(",").quote("").pattern("d.M.yy H:m") } }
                attribute("compound") {
                    type {
                        attributeList()
                            .delimiter(",").quote("")
                            .valueDelimiter("=").valueQuote("\"")
                    }
                }
                attribute("nested1") { type { long() } }
                attribute("nested2") { type { long() } }

                attribute("group_code") { type { string() }.nullable() }
                attribute("group_name") { type { string() }.nullable() }
            }

        }
        loadEntity("onec-product") {
            fromSources { source { file("test-attributes-xls").sheet("test-stock").stopOnEmptyRow() } }
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
                    field("nested1") { parent("compound") }
                    field("nested2") { parent("compound") }
                }
                extra("group") {
                    field("group_code") { column(0); pattern("^G[0-9]{3,3}$") }
                    field("group_name") { column(1) }
                }
            }
        }
        loadEntity("onec-product") {
            fromSources { source { file("test-attributes-csv").sheet("default").stopOnEmptyRow() } }
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
                    field("nested1") { parent("compound") }
                    field("nested2") { parent("compound") }
                }
                extra("group") {
                    field("group_code") { column(0); pattern("^G[0-9]{3,3}$") }
                    field("group_name") { column(1) }
                }
            }
        }
    }
    mediator {
        crossProductionLine {
            input {
                entity("onec-product")
            }
            output("import-product") {
                inheritFrom("onec-product")
            }
        }
    }
}
