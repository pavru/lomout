@file:Import("onecGroupToLong.plugin.kts")
@file:Import("groupPathFromRelation.plugin.kts")
@file:Import("groupAddLeadingG.plugin.kts")
@file:Import("categoryPathFromRelation.plugin.kts")

val copyLong = { value: Long -> value }
val copyString = { value: String -> value }

config {
    database {
        name("magemediation")
        server {
            host("localhost")
            port(3306)
            user("root")
            password("root")
        }
    }
    loader {
        files {
            id("onec-product") assignTo path("E:/home/alexander/Development/Web/oooast-tools/.testdata/test-products.xls")
            id("onec-group") assignTo path("E:/home/alexander/Development/Web/oooast-tools/.testdata/test-products.xlsx")
            id("mage-product") assignTo path("E:/home/alexander/Development/Web/oooast-tools/.testdata/catalog_product.csv")
            id("mage-user-group") assignTo path("E:/home/alexander/Development/Web/oooast-tools/.testdata/customer_group.csv")
            id("mage-category") assignTo path("E:/home/alexander/Development/Web/oooast-tools/.testdata/catalog_category.csv")
            id("mage-customer-group") assignTo path("E:/home/alexander/Development/Web/oooast-tools/.testdata/customer_group.csv")
            id("mage-adv-price") assignTo path("E:/home/alexander/Development/Web/oooast-tools/.testdata/advanced_pricing.csv")
            id("mage-stock-source") assignTo path("E:/home/alexander/Development/Web/oooast-tools/.testdata/stock_sources.csv")
        }

        onecGroup(
            name = "onec-group",
            rowsToSkip = 1,
            maxAbsentDays = 10
        ) {
            sources { source { file("onec-group").sheet("Group").stopOnEmptyRow() } }
            main("group") {
                field("group_code") {
                    column(0)
                    pattern("^G[0-9]{3,3}$")
                    withTransform(onecGroupToLongPlugin)
                } to attribute("group_code") { type { int() }.key() }
                field("group_name") { column(1) }
                field("path") to attribute("path") {
                    type { string() }.withBuilder(groupPathFromRelationPlugin) {
                        parameter<String>("separator") to "/"
                        parameter<String>("root") to "/Root Catalog/Default Category/Каталог"
                    }
                }
            }
        }
        onecGroupRelation(
            name = "onec-group-relation",
            maxAbsentDays = 10
        ) {
            sources { source { file("onec-group").sheet("GroupSiteX").stopOnEmptyRow() } }
            main("group-relation") {
                field("group_code") { column(0) } to attribute { type { int() }.key() }
                field("group_parent_code") { column(1) } to attribute { type { int() }.nullable() }
                field("group_name") { column(2) }
            }
        }
        onecProduct(
            name = "onec-product",
            rowsToSkip = 4,
            maxAbsentDays = 10
        ) {
            sources {
                source { file("onec-product").sheet("stock").stopOnEmptyRow() }
            }
            main("product") {
                field("sku") { column(0).pattern("^[0-9]{4,10}$") } to attribute { type { int() }.key() }
                field("catalog_sku") { column(1) }
                field("russian_name") { column(4) }
                field("english_name") { column(5) }
                field("manufacturer") { column(10) }
                field("country_of_manufacture") { column(8) }
                field("machine_vendor") { column(6) }
                field("machine") { column(7) }
                // TODO: 07.01.2019 in production should be open
                //field("machine_unit") { column(0) }
                field("weight") { column(9) } to attribute { type { double() } }
            }
            secondary("group") {
                field("group_code") {
                    column(0)
                    pattern("^G[0-9]{3,3}$")
                    withTransform(onecGroupToLongPlugin)
                } to attribute("group_code") { type { int() } }
                field("group_name") { column(1) }
            }
        }
        mageProduct(
            name = "mage-product",
            headersRow = 0,
            rowsToSkip = 0,
            maxAbsentDays = 10
        ) {
            sources { source { file("mage-product").sheet(Regex(".*")) } }
            main("product") {
                field("sku") to attribute("sku") { type { string() }.key() }
                field("additional_attributes") {

                } to attribute("additional_attributes") {
                    type {
                        attributeList().quote("").delimiter(",").valueQuote("\"").valueDelimiter("=")
                    }
                }
                field("description") to attribute() { type { text() } }
                field("short_description") to attribute() { type { text() } }
                field("weight") to attribute() { type { double() }.nullable() }
                field("product_online") to attribute() { type { bool() } }
                field("price") to attribute() { type { double() } }
                field("special_price") to attribute() { type { double() }.nullable() }
                field("special_price_from_date") to attribute() { type { date().pattern("d.M.yy") }.nullable() }
                field("special_price_to_date") to attribute() { type { date().pattern("d.M.yy") }.nullable() }
                field("created_at") to attribute() { type { datetime().pattern("d.M.yy, H:m") } }
                field("updated_at") to attribute() { type { datetime().pattern("d.M.yy, H:m") } }
                field("new_from_date") to attribute() { type { date().pattern("d.M.yy") }.nullable() }
                field("new_to_date") to attribute() { type { date().pattern("d.M.yy") }.nullable() }
                field("qty") to attribute() { type { double() } }
                field("out_of_stock_qty") to attribute() { type { double() } }
                field("use_config_min_qty") to attribute() { type { bool() } }
                field("is_qty_decimal") to attribute() { type { bool() } }
                field("allow_backorders") to attribute() { type { bool() } }
                field("use_config_backorders") to attribute() { type { bool() } }
                field("min_cart_qty") to attribute() { type { double() } }
                field("use_config_min_sale_qty") to attribute() { type { bool() } }
                field("max_cart_qty") to attribute() { type { double() } }
                field("use_config_max_sale_qty") to attribute() { type { bool() } }
                field("is_in_stock") to attribute() { type { bool() } }
                field("notify_on_stock_below") to attribute() { type { double() } }
                field("use_config_notify_stock_qty") to attribute() { type { bool() } }
                field("manage_stock") to attribute() { type { bool() } }
                field("use_config_manage_stock") to attribute() { type { bool() } }
                field("use_config_qty_increments") to attribute() { type { bool() } }
                field("qty_increments") to attribute() { type { double() } }
                field("use_config_enable_qty_inc") to attribute() { type { bool() } }
                field("enable_qty_increments") to attribute() { type { bool() } }
                field("is_decimal_divided") to attribute() { type { bool() } }
                field("website_id") to attribute() { type { int() } }
                field("related_skus") to attribute() { type { stringList().quote("").delimiter(",") } }
                field("crosssell_skus") to attribute() { type { stringList().quote("").delimiter(",") } }
                field("upsell_skus") to attribute() { type { stringList().quote("").delimiter(",") } }
                field("additional_images") to attribute() { type { stringList().quote("").delimiter(",") } }
                field("additional_image_labels") to attribute() { type { stringList().quote("").delimiter(",") } }
                field("associated_skus") to attribute() { type { stringList().quote("").delimiter(",") } }
                field("ratings_summary") { parent("additional_attributes") } to attribute() { type { double() }.nullable() }
                field("cost") { parent("additional_attributes") } to attribute("cost") { type { double() }.nullable() }
                // todo remove optional in this section
                field("english_name") { parent("additional_attributes") } to attribute("english_name") { type { string() }.nullable() }
                field("catalog_sku") { parent("additional_attributes") } to attribute("catalog_sku") { type { string() }.nullable() }
                field("machine") { parent("additional_attributes") } to attribute("machine") { type { string() }.nullable() }
                field("machine_unit") { parent("additional_attributes") } to attribute("machine_unit") { type { string() }.nullable() }
                field("machine_vendor") { parent("additional_attributes") } to attribute("machine_vendor") { type { string() }.nullable() }
                // end of todo block
                field("ts_dimensions_length") { parent("additional_attributes") } to attribute() { type { double() }.nullable() }
                field("ts_dimensions_height") { parent("additional_attributes") } to attribute() { type { double() }.nullable() }
                field("ts_dimensions_width") { parent("additional_attributes") } to attribute() { type { double() }.nullable() }
            }
        }
        mageCategory(
            name = "mage-category",
            headersRow = 0,
            maxAbsentDays = 10
        ) {
            sources { source { file("mage-category").sheet(Regex(".*")).stopOnEmptyRow() } }
            main("category") {
                field("entity_type_id") to attribute() { type { int() }.nullable() }
                field("attribute_set_id") to attribute() { type { int() } }
                field("created_at") to attribute() { type { datetime().pattern("y-M-d H:m:s") } }
                field("updated_at") to attribute() { type { datetime().pattern("y-M-d H:m:s") } }
                field("parent_id") to attribute() { type { int() } }
                field("increment_id") to attribute() { type { int() }.nullable() }
                field("entity_id") to attribute() { type { int() }.key() }
                field("children") to attribute() { type { string() }.nullable() }
                field("children_count") to attribute() { type { int() } }
                field("description") to attribute() { type { text() } }
                field("include_in_menu") to attribute() { type { bool() } }
                field("is_active") to attribute() { type { bool() }.nullable() }
                field("is_anchor") to attribute() { type { bool() }.nullable() }
                field("is_virtual_category") to attribute() { type { bool() }.nullable() }
                field("level") to attribute() { type { int() } }
                field("position") to attribute() { type { int() } }
                field("use_name_in_product_search") to attribute() { type { bool() } }
                field("gen_store_id") to attribute() { type { int() } }
                field("gen_products") to attribute() { type { intList().quote("").delimiter("|") }.nullable() }
            }
        }
        mageCustomerGroup(
            name = "mage-customer-group",
            headersRow = 0,
            maxAbsentDays = 10
        ) {
            sources { source { file("mage-customer-group").sheet(Regex(".*")).stopOnEmptyRow() } }
            main("customer-group") {
                field("customer_group_id") to attribute() { type { int() }.key() }
                field("tax_class_id") to attribute() { type { int() }.key() }
            }
        }
        magePrice(
            name = "mage-adv-price",
            headersRow = 0,
            maxAbsentDays = 10
        ) {
            sources { source { file("mage-adv-price").sheet(Regex(".*")).stopOnEmptyRow() } }
            main("mage-price") {
                field("sku") to attribute { type { string() }.key() }
                field("tier_price_website") to attribute { type { string() }.key() }
                field("tier_price_customer_group") to attribute { type { string() }.key() }
                field("tier_price_qty") to attribute { type { double() } }
                field("tier_price") to attribute { type { double() } }
            }
        }
        mageInventory(
            name = "mage-stock-source",
            headersRow = 0,
            maxAbsentDays = 10
        ) {
            sources { source { file("mage-stock-source").sheet(Regex(".*")).stopOnEmptyRow() } }
            main("stock-source") {
                field("source_code") to attribute { type { string() }.key() }
                field("sku") to attribute { type { string() }.key() }
                field("status") to attribute { type { bool() } }
                field("quantity") to attribute { type { double() } }
            }
        }
    }
    mediator {
        onec {
            group {
                idAttribute("group_code") { type { string() } }
                pathAttribute("__path") {
                    type { string() }
                    withBuilder(groupPathFromRelationPlugin) {
                        parameter<String>("separator") to "/"
                        parameter<String>("root") to "/Root Catalog/Default Category/Каталог"
                    }
                }
            }
        }
        magento {
            category {
                pathAttribute("__path") {
                    type { string() }
                    withBuilder(categoryPathFromRelationPlugin) {
                        parameter<String>("separator") to "/"
                        parameter<String>("root") to "/"
                    }
                }
                idAttribute("entity_id") {
                    type { int() }
                }
            }
        }
        mapping {
            categories {
                onecID("G001") to mageID(22)
                onecID("G002") to magePath("/Root Catalog/Default Category/Каталог/Прочие запасные части/Автопокрышки")
                mageID(22) to onecID("G001")
                magePath("/Root Catalog/Default Category/Каталог/Прочие запасные части/Автопокрышки") to onecID("G002")
                mageID(102) to onecPath("G54321")
                onecID("G12345") to magePath("/RootCatalog/Test")
                // attribute mapping mage->onec
                mageAttribute("entity_id").transformTo(
                    onecAttribute("group_code") { type { int() } },
                    copyLong
                )
                mageAttribute("parent").transformTo(
                    onecAttribute("group_parent_code") { type { int() } },
                    copyLong
                )
                mageAttribute("name").transformTo(
                    onecAttribute("group_name") { type { string() } },
                    copyString
                )
                // attribute mapping onec->mage
            }
        }
    }
}
