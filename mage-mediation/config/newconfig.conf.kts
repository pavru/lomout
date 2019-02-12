@file:Import("onecGroupToLong.plugin.kts")
@file:Import("groupPathFromRelation.plugin.kts")
@file:Import("groupAddLeadingG.plugin.kts")
@file:Import("categoryPathFromRelation.plugin.kts")
@file:Import("matcher/CategoryMatcher.plugin.kts")
@file:Import("processor/MatchedCategoryProcessor.plugin.kts")
@file:Import("processor/UnMatchedCategoryProcessor.plugin.kts")
@file:Import("processor/UnMatchedGroupProcessor.plugin.kts")

import matcher.CategoryMatcher_plugin.CategoryMatcher
import processor.MatchedCategoryProcessor_plugin.MatchedCategoryProcessor
import processor.UnMatchedCategoryProcessor_plugin.UnMatchedCategoryProcessor
import processor.UnMatchedGroupProcessor_plugin.UnMatchedGroupProcessor

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
            file("onec-product") { path("E:/home/alexander/Development/Web/oooast-tools/.testdata/test-products.xls") }
            file("onec-group") { path("E:/home/alexander/Development/Web/oooast-tools/.testdata/test-products.xlsx") }
            file("mage-product") { path("E:/home/alexander/Development/Web/oooast-tools/.testdata/catalog_product.csv") }
            file("mage-user-group") { path("E:/home/alexander/Development/Web/oooast-tools/.testdata/customer_group.csv") }
            file("mage-category") { path("E:/home/alexander/Development/Web/oooast-tools/.testdata/catalog_category.csv") }
            file("mage-customer-group") { path("E:/home/alexander/Development/Web/oooast-tools/.testdata/customer_group.csv") }
            file("mage-adv-price") { path("E:/home/alexander/Development/Web/oooast-tools/.testdata/advanced_pricing.csv") }
            file("mage-stock-source") { path("E:/home/alexander/Development/Web/oooast-tools/.testdata/stock_sources.csv") }
        }
        entities {
            entity("onec-group", true) {
                attribute("group_code") { type { long() }.key() }
                attribute("group_name") { type { string() } }
                attribute("path") {
                    type { string() }.withBuilder(groupPathFromRelationPlugin) {
                        parameter<String>("separator") to "/"
                        parameter<String>("root") to "/Root Catalog/Default Category/Каталог"
                    }
                }
            }
            entity("onec-group-relation") {
                inheritFrom("onec-group")
                attribute("group_parent_code") { type { long() }.nullable() }
            }
            entity("onec-product", true) {
                attribute("sku") { type { long() }.key() }
                attribute("weight") { type { double() } }
                attribute("group_code") { type { long() } }
            }
            entity("mage-product", true) {
                setOpen()
                attribute("sku") { type { string() }.key() }
                attribute("additional_attributes") {
                    type {
                        attributeList().quote("").delimiter(",").valueQuote("\"").valueDelimiter("=")
                    }
                }
                attribute("description") { type { text() } }
                attribute("short_description") { type { text() } }
                attribute("weight") { type { double() }.nullable() }
                attribute("product_online") { type { bool() } }
                attribute("price") { type { double() } }
                attribute("special_price") { type { double() }.nullable() }
                attribute("special_price_from_date") { type { date().pattern("d.M.yy") }.nullable() }
                attribute("special_price_to_date") { type { date().pattern("d.M.yy") }.nullable() }
                attribute("created_at") { type { datetime().pattern("d.M.yy, H:m") } }
                attribute("updated_at") { type { datetime().pattern("d.M.yy, H:m") } }
                attribute("new_from_date") { type { date().pattern("d.M.yy") }.nullable() }
                attribute("new_to_date") { type { date().pattern("d.M.yy") }.nullable() }
                attribute("qty") { type { double() } }
                attribute("out_of_stock_qty") { type { double() } }
                attribute("use_config_min_qty") { type { bool() } }
                attribute("is_qty_decimal") { type { bool() } }
                attribute("allow_backorders") { type { bool() } }
                attribute("use_config_backorders") { type { bool() } }
                attribute("min_cart_qty") { type { double() } }
                attribute("use_config_min_sale_qty") { type { bool() } }
                attribute("max_cart_qty") { type { double() } }
                attribute("use_config_max_sale_qty") { type { bool() } }
                attribute("is_in_stock") { type { bool() } }
                attribute("notify_on_stock_below") { type { double() } }
                attribute("use_config_notify_stock_qty") { type { bool() } }
                attribute("manage_stock") { type { bool() } }
                attribute("use_config_manage_stock") { type { bool() } }
                attribute("use_config_qty_increments") { type { bool() } }
                attribute("qty_increments") { type { double() } }
                attribute("use_config_enable_qty_inc") { type { bool() } }
                attribute("enable_qty_increments") { type { bool() } }
                attribute("is_decimal_divided") { type { bool() } }
                attribute("website_id") { type { long() } }
                attribute("related_skus") { type { stringList().quote("").delimiter(",") } }
                attribute("crosssell_skus") { type { stringList().quote("").delimiter(",") } }
                attribute("upsell_skus") { type { stringList().quote("").delimiter(",") } }
                attribute("additional_images") { type { stringList().quote("").delimiter(",") } }
                attribute("additional_image_labels") { type { stringList().quote("").delimiter(",") } }
                attribute("associated_skus") { type { stringList().quote("").delimiter(",") } }
                attribute("ratings_summary") { type { double() }.nullable() }
                attribute("cost") { type { double() }.nullable() }
                // todo remove optional in this section
                attribute("english_name") { type { string() }.nullable() }
                attribute("catalog_sku") { type { string() }.nullable() }
                attribute("machine") { type { string() }.nullable() }
                attribute("machine_unit") { type { string() }.nullable() }
                attribute("machine_vendor") { type { string() }.nullable() }
                // end of todo block
                attribute("ts_dimensions_length") { type { double() }.nullable() }
                attribute("ts_dimensions_height") { type { double() }.nullable() }
                attribute("ts_dimensions_width") { type { double() }.nullable() }
            }
            entity("mage-category", true) {
                setOpen()
                attribute("entity_type_id") { type { long() }.nullable() }
                attribute("attribute_set_id") { type { long() } }
                attribute("created_at") { type { datetime().pattern("y-M-d H:m:s") } }
                attribute("updated_at") { type { datetime().pattern("y-M-d H:m:s") } }
                attribute("parent_id") { type { long() } }
                attribute("increment_id") { type { long() }.nullable() }
                attribute("entity_id") { type { long() }.key() }
                attribute("children") { type { string() }.nullable() }
                attribute("children_count") { type { long() } }
                attribute("description") { type { text() } }
                attribute("include_in_menu") { type { bool() } }
                attribute("is_active") { type { bool() }.nullable() }
                attribute("is_anchor") { type { bool() }.nullable() }
                attribute("is_virtual_category") { type { bool() }.nullable() }
                attribute("level") { type { long() } }
                attribute("position") { type { long() } }
                attribute("use_name_in_product_search") { type { bool() } }
                attribute("gen_store_id") { type { long() } }
                attribute("gen_products") { type { longList().quote("").delimiter("|") }.nullable() }
            }
            entity("mage-customer-group", true) {
                attribute("customer_group_id") { type { long() }.key() }
                attribute("tax_class_id") { type { long() }.key() }
            }
            entity("mage-adv-price", true) {
                attribute("sku") { type { string() }.key() }
                attribute("tier_price_website") { type { string() }.key() }
                attribute("tier_price_customer_group") { type { string() }.key() }
                attribute("tier_price_qty") { type { double() } }
                attribute("tier_price") { type { double() } }
            }
            entity("mage-stock-source", true) {
                attribute("source_code") { type { string() }.key() }
                attribute("sku") { type { string() }.key() }
                attribute("status") { type { bool() } }
                attribute("quantity") { type { double() } }
            }
        }

        loadEntity("onec-group") {
            fromSources {
                source { file("onec-group").sheet("Group").stopOnEmptyRow() }
            }
            rowsToSkip(1)
            keepAbsentForDays(10)

            sourceFields {
                main("group") {
                    field("group_code") {
                        column(0)
                        pattern("^G[0-9]{3,3}$")
                        withTransform(onecGroupToLongPlugin)
                    }
                    field("group_name") { column(1) }
                    field("path")
                }
            }
        }
        loadEntity("onec-group-relation") {
            keepAbsentForDays(10)
            fromSources { source { file("onec-group").sheet("GroupSiteX").stopOnEmptyRow() } }
            sourceFields {
                main("group-relation") {
                    field("group_code") { column(0) } to attribute("group_code")
                    field("group_parent_code") { column(1) }
                    field("group_name") { column(2) }
                }
            }
        }
        loadEntity("onec-product") {
            rowsToSkip(4)
            keepAbsentForDays(10)
            fromSources { source { file("onec-product").sheet("stock").stopOnEmptyRow() } }
            sourceFields {
                main("product") {
                    field("sku") { column(0).pattern("^[0-9]{4,10}$") } to attribute("sku")
                    field("catalog_sku") { column(1) }
                    field("russian_name") { column(4) }
                    field("english_name") { column(5) }
                    field("manufacturer") { column(10) }
                    field("country_of_manufacture") { column(8) }
                    field("machine_vendor") { column(6) }
                    field("machine") { column(7) }
                    // TODO: 07.01.2019 in production should be open
                    //field("machine_unit") { column(0) }
                    field("weight") { column(9) }
                }
                extra("group") {
                    field("group_code") {
                        column(0)
                        pattern("^G[0-9]{3,3}$")
                        withTransform(onecGroupToLongPlugin)
                    }
                    field("group_name") { column(1) }
                }
            }
        }
        loadEntity("mage-product") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-product").sheet(Regex(".*")) } }
            sourceFields {
                main("product") {
                    field("additional_attributes")
                    field("ratings_summary") { parent("additional_attributes") }
                    field("cost") { parent("additional_attributes") }
                    // todo remove optional in this section
                    field("english_name") { parent("additional_attributes") }
                    field("catalog_sku") { parent("additional_attributes") }
                    field("machine_unit") { parent("additional_attributes") }
                    field("machine_vendor") { parent("additional_attributes") }
                    // end of todo block
                    field("ts_dimensions_length") { parent("additional_attributes") }
                    field("ts_dimensions_height") { parent("additional_attributes") }
                    field("ts_dimensions_width") { parent("additional_attributes") }
                }
            }
        }
        loadEntity("mage-category") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-category").sheet(Regex(".*")).stopOnEmptyRow() } }
            sourceFields {
                main("category") {}
            }
        }
        loadEntity("mage-customer-group") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-customer-group").sheet(Regex(".*")).stopOnEmptyRow() } }
            sourceFields {
                main("customer-group") {}
            }
        }
        loadEntity("mage-adv-price") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-adv-price").sheet(Regex(".*")).stopOnEmptyRow() } }
            sourceFields {
                main("mage-price") {}
            }
        }
        loadEntity("mage-stock-source") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-stock-source").sheet(Regex(".*")).stopOnEmptyRow() } }
            sourceFields {
                main("stock-source") {}
            }
        }
    }

    mediator {
        productionLine {
            fromEntities {
                sourceEntity("onec-group") {
                    (attribute("group_code") to
                            attribute("entity_id") { type { long() } })
                        .withTransform<Long?, Long?> {
                            if (it == null) {
                                null
                            } else {
                                mapOf<Long, Long>(
                                    1L to 22L,
                                    999L to 34L
                                )[it]
                            }
                        }
                }
                sourceEntity("mage-category")
            }
            toEntity("import-category") {
                inheritFrom("mage-category")
            }
            matcher<CategoryMatcher>()

            processors {
                matched<MatchedCategoryProcessor>()
                unmatched<UnMatchedGroupProcessor>("onec-group")
                unmatched<UnMatchedCategoryProcessor>("mage-category")
            }
        }

        entities {
            entity("category", true) {
                inheritFrom("mage-category") {
                    exclude("gen_store_id")
                }
                attribute("group_code") { type { long() }.key() }
                attribute("group_name") { type { string() } }
                attribute("__path") {
                    type { string() }.withBuilder(groupPathFromRelationPlugin) {
                        parameter<String>("separator") to "/"
                        parameter<String>("root") to "/Root Catalog/Default Category/Каталог"
                    }
                }
            }
        }

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
                    type { long() }
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
                    onecAttribute("group_code") { type { long() } },
                    copyLong
                )
                mageAttribute("parent").transformTo(
                    onecAttribute("group_parent_code") { type { long() } },
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
