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
        entities {
            entity("test-entity-1", false) {
                attribute<STRING>("string-attr-1") {
                    reader { _, _ -> STRING("test reader function") }
                }
                attribute<TEXT>("text-attr-1") { key() }
                attribute<BOOLEAN>("boolean-attr-1") { nullable() }
                attribute<LONG>("long-attr-1")
                attribute<DOUBLE>("double-attr-1")
            }
            entity("test-entity-2", true) {
                attribute<STRINGLIST>("string-list-attr-2")
                attribute<BOOLEANLIST>("boolean-list-attr-2")
                attribute<LONGLIST>("long-list-attr-2")
                attribute<DOUBLELIST>("double-list-2")
                attribute<ATTRIBUTELIST>("attribute-list-2")
            }
            entity("onec-product", false) {
                attribute<STRING>("sku") { key() }
                attribute<TEXT>("description")
                attribute<BOOLEAN>("bool_val")
                attribute<LONG>("long_val") {
                    reader<LongAttributeReader> { locale = "ru_RU" }
                }
                attribute<DOUBLE>("double_val") {
                    reader<DoubleAttributeReader> { locale = "ru_RU" }
                }
                attribute<DATE>("date_val") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                }
                attribute<DATETIME>("datetime_val") {
                    reader<DateTimeAttributeReader> { pattern = "d.M.yy H:m" }
                }
                attribute<STRINGLIST>("string_list") {
                    reader<StringListAttributeReader> { delimiter = ','; quote = null }
                }
                attribute<BOOLEANLIST>("bool_list") {
                    reader<BooleanListAttributeReader> { delimiter = ','; quote = null }
                }
                attribute<LONGLIST>("long_list") {
                    reader<LongListAttributeReader> { delimiter = ','; quote = null }
                }
                attribute<DOUBLELIST>("double_list") {
                    reader<DoubleListAttributeReader> { locale = "ru_RU";delimiter = '|'; quote = null }
                }
                attribute<DATELIST>("date_list") {
                    reader<DateListAttributeReader> { delimiter = ',';quote = null;pattern = "d.M.yy" }
                }
                attribute<DATETIMELIST>("datetime_list") {
                    reader<DateTimeListAttributeReader> { delimiter = ',';quote = null;pattern = "d.M.yy H:m" }
                }
                attribute<ATTRIBUTELIST>("compound") {
                    reader<AttributeListReader> {
                        delimiter = ',';quote = null;valueDelimiter = '=';valueQuote = '"'
                    }
                }
                attribute<LONG>("nested1")
                attribute<LONG>("nested2")

                attribute<STRING>("group_code") { nullable() }
                attribute<STRING>("group_name") { nullable() }
            }
        }
        loadEntity("onec-product") {
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
        productionLine {
            input {
                entity("onec-product") {
                    statuses(EntityStatus.CREATED,EntityStatus.UPDATED,EntityStatus.UNCHANGED)
                }
            }
            output("import-product") {
                copyFrom("onec-product")
            }
            pipeline {
                assembler { _, _ -> emptyMap() }
            }
        }
        productionLine {
            input {
                entity("onec-product") {
                    statuses(EntityStatus.CREATED,EntityStatus.UPDATED,EntityStatus.UNCHANGED)
                }
            }
            output("import-product")
            pipeline {
                assembler { _, _ -> emptyMap() }
            }
        }
    }
}
