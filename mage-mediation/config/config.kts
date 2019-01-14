import net.pototskiy.apps.magemediation.api.config.DatasetTarget
import net.pototskiy.apps.magemediation.api.config.EmptyRowAction
import net.pototskiy.apps.magemediation.dsl.config.config
import net.pototskiy.apps.magemediation.plugins.loader.OnecGroupAddLeadingG
import net.pototskiy.apps.magemediation.plugins.medium.CategoryPathFromRelationBuilder
import net.pototskiy.apps.magemediation.plugins.medium.GroupPathFromCodeBuilder

config {
    database {
        name = "magemediation"
        server {
            host = "localhost"
            port = 3306
            user = "root"
            password = "root"
        }
    }
    loader {
        files {
            id("onec-product") assignedToPath "E:/home/alexander/Development/Web/oooast-tools/.testdata/test-products.xls"
            id("onec-group").assignedToPath("E:/home/alexander/Development/Web/oooast-tools/.testdata/test-products.xlsx")
            "mage-product" isIdOf "E:/home/alexander/Development/Web/oooast-tools/.testdata/catalog_product.csv"
            "mage-user-group" isIdOf "E:/home/alexander/Development/Web/oooast-tools/.testdata/customer_group.csv"
            "mage-group" isIdOf "E:/home/alexander/Development/Web/oooast-tools/.testdata/catalog_category.csv"
            "mage-customer-group" isIdOf "E:/home/alexander/Development/Web/oooast-tools/.testdata/customer_group.csv"
            path("E:/home/alexander/Development/Web/oooast-tools/.testdata/advanced_pricing.csv").linkedToID("mage-adv-price")
            path("E:/home/alexander/Development/Web/oooast-tools/.testdata/stock_sources.csv") linkedToID "mage-stock-source"
        }
        datasets {
            dataset(
                name = "onec-group",
                target = DatasetTarget.ONEC_GROUP,
                rowsToSkip = 1,
                maxAbsentDays = 10
            ) {
                sources {
                    source { file("onec-group").sheet("Group").emptyRowAction(EmptyRowAction.STOP) }
                }
                fieldSets {
                    main("group") {
                        field("group_code") { column(0).regex("^G[0-9]{3,3}$").key() }
                        field("group_name") { column(1) }
                    }
                }
            }
            dataset(
                name = "onec-group-relation",
                target = DatasetTarget.ONEC_GROUP_RELATION,
                maxAbsentDays = 10
            ) {
                sources {
                    source { file("onec-group").sheet("GroupSiteX").emptyRowAction(EmptyRowAction.STOP) }
                }
                fieldSets {
                    main("group-relation") {
                        field("group_code") {
                            column(0).key()
                            transformer(OnecGroupAddLeadingG::class)
                        }
                        field("group_parent_code") {
                            column(1).optional()
                            transformer(OnecGroupAddLeadingG::class)
                        }
                        field("group_name") { column(2) }
                    }
                }
            }
            dataset(
                name = "onec-product",
                rowsToSkip = 4,
                maxAbsentDays = 10,
                target = DatasetTarget.ONEC_PRODUCT
            ) {
                sources {
                    source { file("onec-product").sheet("stock").emptyRowAction(EmptyRowAction.STOP) }
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
                        //field("machine_unit") { column(0) }
                        field("weight") { column(9).type { double() } }
                    }
                    additional("group") {
                        field("group_code") { column(0).regex("^G[0-9]{3,3}$") }
                        field("group_name") { column(1) }
                    }
                }
            }
            dataset(
                name = "mage-product",
                headersRow = 0,
                rowsToSkip = 0,
                maxAbsentDays = 10,
                target = DatasetTarget.MAGE_PRODUCT
            ) {
                sources {
                    source { file("mage-product").sheet(".*") }
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
                name = "mage-medium",
                headersRow = 0,
                maxAbsentDays = 10,
                target = DatasetTarget.MAGE_CATEGORY
            ) {
                sources {
                    source { file("mage-group").sheet(".*").emptyRowAction(EmptyRowAction.STOP) }
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
                idAttribute(attribute = "group_code") { type { string() } }
                pathAttribute {
                    type { string() }
                    separator("", "/")
                    root("/Root Catalog/Default Category/Каталог")
                    synthetic {
                        klass(GroupPathFromCodeBuilder::class)
                    }
                }
            }
        }
        magento {
            category {
                pathAttribute("path") {
                    type { string() }
                    root("")
                    separator("/", "/")
                    synthetic {
                        klass(CategoryPathFromRelationBuilder::class)
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
                onecID("G002") to magePath("Root Catalog/Default Category/Каталог/Прочие запасные части/Автопокрышки")
                mageID(22) to onecID("G001")
                magePath("/Root Catalog/Default Category/Каталог/Прочие запасные части/Автопокрышки") to onecID("G002")
                mageID(102) to onecPath("G54321")
                onecID("G12345") to magePath("RootCatalog/Test")
            }
        }
    }
}