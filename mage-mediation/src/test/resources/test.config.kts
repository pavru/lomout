import net.pototskiy.apps.magemediation.api.config.DatasetTarget
import net.pototskiy.apps.magemediation.api.config.EmptyRowAction
import net.pototskiy.apps.magemediation.dsl.config.config

config {
    database {
        name = "magemediation"
        server {
            host = "localhost"
            user = "root"
            password = "root"
        }
    }
    loader {
        files {
            "onec_product" isIdOf "../.testdata/test-products.xls"
            "onec_group" isIdOf "../.testdata/test-products.xlsx"
            "mage_product" isIdOf "../.testdata/catalog_product.csv"
            "mage_user_group" isIdOf "../.testdata/customer_group.csv"
            "mage_group" isIdOf "../.testdata/catalog_category.csv"
            "mage_customer_group" isIdOf "../.testdata/customer_group.csv"
            "mage_adv_price" isIdOf "../.testdata/advanced_pricing.csv"
            "mage-stock-source" isIdOf "../.testdata/stock_sources.csv"
        }
        datasets {
            dataset(
                name = "onec-test-product",
                rowsToSkip = 3,
                maxAbsentDays = 10,
                target = DatasetTarget.ONEC_PRODUCT
            ) {
                sources {
                    source { file("onec_product").sheet("stock").emptyRowAction(EmptyRowAction.STOP) }
                }
                fieldSets {
                    main("product") {
                        field("sku") { column(0).regex("^[0-9]+$").key() }
                        field("description") { column(1).type { text() } }
                        field("bool_val") { column(2).type { bool() } }
                        field("int_val") { column(3).type { int() } }
                        field("double_val") { column(4).type { double().locale("ru_RU") } }
                        field("date_val") { column(5).type { date().pattern("d.M.yy") } }
                        field("datetime_val") { column(6).type { datetime().pattern("d.M.yy H:m") } }
                        field("string_list") { column(7).type { stringList().quote("").delimiter(",") } }
                        field("bool_list") { column(8).type { boolList().quote("").delimiter(",") } }
                        field("int_list") { column(9).type { intList().quote("").delimiter(",") } }
                        field("double_list") {
                            column(10).type {
                                doubleList().locale("ru_RU").quote("").delimiter("|")
                            }
                        }
                        field("date_list") {
                            column(11).type {
                                dateList().quote("").delimiter(",").pattern("d.M.yy")
                            }
                        }
                        field("datetime_list") {
                            column(12).type {
                                datetimeList().quote("").delimiter(",").pattern("d.M.yy H:m")
                            }
                        }
                        field("compound") {
                            column(13).type {
                                attributeList()
                                    .quote("").delimiter(",")
                                    .valueQuote("\"").valueDelimiter("=")
                            }
                        }
                        field("nested1") { nested().parent("compound").type { int() } }
                        field("nested2") { nested().parent("compound").type { int() } }
                    }
                    additional("group") {
                        field("group_code") { column(0).regex("^G[0-9]{3,3}$") }
                        field("group_name") { column(1) }
                    }
                }
            }
            dataset(
                name = "headers-test-product",
                target = DatasetTarget.ONEC_PRODUCT,
                headersRow = 2,
                rowsToSkip = 3,
                maxAbsentDays = 10
            ) {
                sources {
                    source { file("onec_product").sheet("stock").emptyRowAction(EmptyRowAction.STOP) }
                }
                fieldSets {
                    main("product") {
                        field("sku") { regex("^[0-9]+$").key() }
                        field("description") { type { text() } }
                        field("bool_val") { type { bool() } }
                        field("int_val") { type { int() } }
                        field("double_val") { type { double().locale("ru_RU") } }
                        field("date_val") { type { date().pattern("d.M.yy") } }
                        field("datetime_val") { type { datetime().pattern("d.M.yy H:m") } }
                        field("string_list") { type { stringList().quote("").delimiter(",") } }
                        field("bool_list") { type { boolList().quote("").delimiter(",") } }
                        field("int_list") { type { intList().quote("").delimiter(",") } }
                        field("double_list") {
                            type {
                                doubleList().quote("").delimiter("|").locale("ru_RU")
                            }
                        }
                        field("date_list") {
                            type {
                                dateList().quote("").delimiter(",").pattern("d.M.yy")
                            }
                        }
                        field("datetime_list") {
                            type {
                                datetimeList().quote("").delimiter(",").pattern("d.M.yy H:m")
                            }
                        }
                        field("compound") {
                            type {
                                attributeList()
                                    .quote("").delimiter(",").valueQuote("\"").valueDelimiter("=")
                            }
                        }
                        field("nested1") { nested().parent("compound").type { int() } }
                        field("nested2") { nested().parent("compound").type { int() } }
                    }
                    additional("group") {
                        field("group_code") { column(0).regex("^G[0-9]{3,3}$") }
                        field("group_name") { column(1) }
                    }
                }
            }
            dataset(
                name = "onec_group",
                rowsToSkip = 1,
                maxAbsentDays = 10,
                target = DatasetTarget.ONEC_GROUP
            ) {
                fieldSets {
                    main("group") {
                        field("group_code") { column(0).regex("^G[0-9]{3,3}$").key() }
                        field("group_name") { column(1) }
                    }
                }
            }
            dataset(
                name = "onec_product",
                rowsToSkip = 4,
                maxAbsentDays = 10,
                target = DatasetTarget.ONEC_PRODUCT
            ) {
                sources {
                    source { file("onec_product").sheet("stock").emptyRowAction(EmptyRowAction.STOP) }
                }
                fieldSets {
                    main("product") {
                        field("sku") { column(0).regex("^[0-9]{4,10}$").key() }
                        field("catalog_sku") { column(1) }
                        field("russian_name") { column(4) }
                        field("english_name") { column(5) }
                        field("manufacturer") { column(10) }
                        field("country_of_manufacture") { column(8) }
                        field("machine_vendor") { column(6) }
                        field("machine") { column(7) }
                        // TODO: 07.01.2019 in production should be open
                        // field("machine_unit") { column(0) }
                        field("weight") { column(9).type { double() } }
                    }
                    additional("group") {
                        field("group_code") { column(0).regex("^G[0-9]{3,3}$") }
                        field("group_name") { column(1) }
                    }
                }
            }
            dataset(
                name = "mage_product",
                headersRow = 0,
                rowsToSkip = 0,
                maxAbsentDays = 10,
                target = DatasetTarget.MAGE_PRODUCT
            ) {
                sources {
                    source { file("mage_product").sheet(".*") }
                }
                fieldSets {
                    main("product") {
                        field("sku") { key() }
                        field("additional_attributes") {
                            type {
                                attributeList().quote("").delimiter(",").valueQuote("\"").valueDelimiter("=")
                            }
                        }
                        field("description") { type { text() } }
                        field("short_description") { type { text() } }
                        field("weight") { type { double() }.optional() }
                        field("product_online") { type { bool() } }
                        field("price") { type { double() } }
                        field("special_price") { type { double() }.optional() }
                        field("special_price_from_date") { type { date().pattern("d.M.yy") }.optional() }
                        field("special_price_to_date") { type { date().pattern("d.M.yy") }.optional() }
                        field("created_at") { type { datetime().pattern("d.M.yy, H:m") } }
                        field("updated_at") { type { datetime().pattern("d.M.yy, H:m") } }
                        field("new_from_date") { type { date().pattern("d.M.yy") }.optional() }
                        field("new_to_date") { type { date().pattern("d.M.yy") }.optional() }
                        field("qty") { type { double() } }
                        field("out_of_stock_qty") { type { double() } }
                        field("use_config_min_qty") { type { bool() } }
                        field("is_qty_decimal") { type { bool() } }
                        field("allow_backorders") { type { bool() } }
                        field("use_config_backorders") { type { bool() } }
                        field("min_cart_qty") { type { double() } }
                        field("use_config_min_sale_qty") { type { bool() } }
                        field("max_cart_qty") { type { double() } }
                        field("use_config_max_sale_qty") { type { bool() } }
                        field("is_in_stock") { type { bool() } }
                        field("notify_on_stock_below") { type { double() } }
                        field("use_config_notify_stock_qty") { type { bool() } }
                        field("manage_stock") { type { bool() } }
                        field("use_config_manage_stock") { type { bool() } }
                        field("use_config_qty_increments") { type { bool() } }
                        field("qty_increments") { type { double() } }
                        field("use_config_enable_qty_inc") { type { bool() } }
                        field("enable_qty_increments") { type { bool() } }
                        field("is_decimal_divided") { type { bool() } }
                        field("website_id") { type { int() } }
                        field("related_skus") { type { stringList().quote("").delimiter(",") } }
                        field("crosssell_skus") { type { stringList().quote("").delimiter(",") } }
                        field("upsell_skus") { type { stringList().quote("").delimiter(",") } }
                        field("additional_images") { type { stringList().quote("").delimiter(",") } }
                        field("additional_image_labels") { type { stringList().quote("").delimiter(",") } }
                        field("associated_skus") { type { stringList().quote("").delimiter(",") } }
                        field("ratings_summary") {
                            type { double() }.nested().parent("additional_attributes").optional()
                        }
                        field("cost") { type { double() }.nested().parent("additional_attributes").optional() }
                        // todo remove optional in this section
                        field("english_name") { nested().parent("additional_attributes").optional() }
                        field("catalog_sku") { nested().parent("additional_attributes").optional() }
                        field("machine") { nested().parent("additional_attributes").optional() }
                        field("machine_unit") { nested().parent("additional_attributes").optional() }
                        field("machine_vendor") { nested().parent("additional_attributes").optional() }
                        // end of todo block
                        field("ts_dimensions_length") {
                            type { double() }.nested().parent("additional_attributes").optional()
                        }
                        field("ts_dimensions_height") {
                            type { double() }.nested().parent("additional_attributes").optional()
                        }
                        field("ts_dimensions_width") {
                            type { double() }.nested().parent("additional_attributes").optional()
                        }
                    }
                }
            }
            dataset(
                name = "mage_group",
                headersRow = 0,
                maxAbsentDays = 10,
                target = DatasetTarget.MAGE_CATEGORY
            ) {
                sources {
                    source { file("mage_group").sheet(".*").emptyRowAction(EmptyRowAction.STOP) }
                }
                fieldSets {
                    main("medium") {
                        field("entity_type_id") { type { int() }.optional() }
                        field("attribute_set_id") { type { int() } }
                        field("created_at") { type { datetime().pattern("y-M-d H:m:s") } }
                        field("updated_at") { type { datetime().pattern("y-M-d H:m:s") } }
                        field("parent_id") { type { int() } }
                        field("increment_id") { type { int() }.optional() }
                        field("entity_id") { type { int() }.key() }
                        field("children") { optional() }
                        field("children_count") { type { int() } }
                        field("description") { type { text() } }
                        field("include_in_menu") { type { bool() } }
                        field("is_active") { type { bool() }.optional() }
                        field("is_anchor") { type { bool() }.optional() }
                        field("is_virtual_category") { type { bool() }.optional() }
                        field("level") { type { int() } }
                        field("position") { type { int() } }
                        field("use_name_in_product_search") { type { bool() } }
                        field("gen_store_id") { type { int() } }
                        field("gen_products") { type { intList().quote("").delimiter("|") }.optional() }
                    }
                }
            }
            dataset(
                name = "mage-customer-group",
                headersRow = 0,
                maxAbsentDays = 10,
                target = DatasetTarget.MAGE_USER_GROUP
            ) {
                fieldSets {
                    main("customer-group") {
                        field("customer_group_id") { type { int() }.key() }
                        field("tax_class_id") { type { int() }.key() }
                    }
                }
            }
            dataset(
                name = "mage-adv-price",
                headersRow = 0,
                maxAbsentDays = 10,
                target = DatasetTarget.MAGE_PRICE
            ) {
                fieldSets {
                    main("mage-price") {
                        field("sku") { key() }
                        field("tier_price_website") { key() }
                        field("tier_price_customer_group") { key() }
                        field("tier_price_qty") { type { double() } }
                        field("tier_price") { type { double() } }
                    }
                }
            }
            dataset(
                name = "mage-stock-source",
                headersRow = 0,
                maxAbsentDays = 10,
                target = DatasetTarget.MAGE_INVENTORY
            ) {
                fieldSets {
                    main("stock-source") {
                        field("source_code") { key() }
                        field("sku") { key() }
                        field("status") { type { bool() } }
                        field("quantity") { type { double() } }
                    }
                }
            }
        }
    }
    mediator {
        onec {
            group {
                idAttribute(attribute = "group_code") {
                    type { string() }
                    structure("(G[0-9]{1,1})([0-9]{1,1})([0-9]{1,1})")
                    subCodeFiller("0")
                    root("")
                    separator("/")
                }
            }
        }
        magento {
            category {
                pathAttribute("path") {
                    type { string() }
                    root("")
                    separator("/")
                }
            }
        }
    }
}
