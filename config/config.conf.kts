@file:DependsOn("commons-validator", "commons-validator", "commons-validator", "1.6")
@file:Import("reader/OnecGroupToLong.plugin.conf.kts")
@file:Import("builder/GroupPathFromRelation.plugin.conf.kts")
@file:Import("builder/CategoryPathFromRelation.plugin.conf.kts")
@file:Import("builder/RelationGroupNameFromGroup.plugin.conf.kts")
@file:Import("reader/GroupToCategoryPath.plugin.conf.kts")
@file:Import("pipeline/classifier/CategoryClassifier.plugin.conf.kts")
@file:Import("pipeline/assembler/MatchedCategoryAssembler.plugin.conf.kts")
@file:Import("pipeline/classifier/EntityTypeClassifier.plugin.conf.kts")
@file:Import("pipeline/assembler/CategoryFromGroupAssembler.plugin.conf.kts")
@file:Import("pipeline/assembler/MarkCategoryToRemove.plugin.conf.kts")

import CategoryClassifier_plugin_conf.CategoryClassifier
import CategoryFromGroupAssembler_plugin_conf.CategoryFromGroupAssembler
import CategoryPathFromRelation_plugin_conf.CategoryPathFromRelation
import EntityTypeClassifier_plugin_conf.EntityTypeClassifier
import GroupPathFromRelation_plugin_conf.GroupPathFromRelation
import GroupToCategoryPath_plugin_conf.GroupToCategoryPath
import MarkCategoryToRemove_plugin_conf.MarkCategoryToRemove
import MatchedCategoryAssembler_plugin_conf.MatchedCategoryAssembler
import OnecGroupToLong_plugin_conf.OnecGroupToLong
import RelationGroupNameFromGroup_plugin_conf.RelationGroupNameFromGroup

config {
    database {
        name("lomout")
        server {
            host("localhost")
            port(3306)
            user("root")
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                password("")
            } else {
                password("root")
            }
        }
    }
    loader {
        files {
            val testDataDir = System.getenv("TEST_DATA_DIR")
            file("onec-data") { path("$testDataDir/test-products.xls") }
            file("mage-product") { path("$testDataDir/catalog_product.csv") }
            file("mage-user-group") { path("$testDataDir/customer_group.csv") }
            file("mage-category") { path("$testDataDir/catalog_category.csv") }
            file("mage-customer-group") { path("$testDataDir/customer_group.csv") }
            file("mage-adv-price") { path("$testDataDir/advanced_pricing.csv") }
            file("mage-stock-source") { path("$testDataDir/stock_sources.csv") }
            file("onec-extended-info") { path("$testDataDir/onec.extended.info.xls") }
        }
        entities {
            entity("onec-group", false) {
                attribute<LONG>("group_code") {
                    key()
                    reader<OnecGroupToLong>()
                }
                attribute<STRING>("group_name")
                attribute<STRING>("__path") {
                    builder<GroupPathFromRelation> {
                        separator = "/"
                        root = "/Root Catalog/Default Category/Каталог/"
                    }
                }
            }
            entity("onec-group-relation", false) {
                attribute<LONG>("group_code") { key() }
                attribute<LONG>("group_parent_code") { nullable() }
                attribute<STRING>("group_name") {
                    builder<RelationGroupNameFromGroup>()
                }
            }
            entity("onec-product", true) {
                attribute<LONG>("sku") { key() }
                attribute<DOUBLE>("weight")
                attribute<LONG>("group_code") {
                    reader<OnecGroupToLong>()
                }
            }
            entity("mage-product", true) {
                attribute<STRING>("sku") { key() }
                attribute<ATTRIBUTELIST>("additional_attributes") {
                    reader<AttributeListReader> {
                        quote = null;delimiter = ',';valueQuote = '"';valueDelimiter = '='
                    }
                }
                attribute<TEXT>("description")
                attribute<TEXT>("short_description") { nullable() }
                attribute<DOUBLE>("weight") { nullable() }
                attribute<BOOLEAN>("product_online")
                attribute<DOUBLE>("price")
                attribute<DOUBLE>("special_price") { nullable() }
                attribute<DATE>("special_price_from_date") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                    nullable()
                }
                attribute<DATE>("special_price_to_date") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                    nullable()
                }
                attribute<DATETIME>("created_at") {
                    reader<DateTimeAttributeReader> { pattern = "d.M.yy, H:m" }
                }
                attribute<DATETIME>("updated_at") {
                    reader<DateTimeAttributeReader> { pattern = "d.M.yy, H:m" }
                }
                attribute<DATE>("new_from_date") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                    nullable()
                }
                attribute<DATE>("new_to_date") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                    nullable()
                }
                attribute<DOUBLE>("qty")
                attribute<DOUBLE>("out_of_stock_qty")
                attribute<BOOLEAN>("use_config_min_qty")
                attribute<BOOLEAN>("is_qty_decimal")
                attribute<BOOLEAN>("allow_backorders")
                attribute<BOOLEAN>("use_config_backorders")
                attribute<DOUBLE>("min_cart_qty")
                attribute<BOOLEAN>("use_config_min_sale_qty")
                attribute<DOUBLE>("max_cart_qty")
                attribute<BOOLEAN>("use_config_max_sale_qty")
                attribute<BOOLEAN>("is_in_stock")
                attribute<DOUBLE>("notify_on_stock_below")
                attribute<BOOLEAN>("use_config_notify_stock_qty")
                attribute<BOOLEAN>("manage_stock")
                attribute<BOOLEAN>("use_config_manage_stock")
                attribute<BOOLEAN>("use_config_qty_increments")
                attribute<DOUBLE>("qty_increments")
                attribute<BOOLEAN>("use_config_enable_qty_inc")
                attribute<BOOLEAN>("enable_qty_increments")
                attribute<BOOLEAN>("is_decimal_divided")
                attribute<LONG>("website_id")
                attribute<STRINGLIST>("related_skus") {
                    reader<StringListAttributeReader> { quote = null;delimiter = ',' }
                    nullable()
                }
                attribute<STRINGLIST>("crosssell_skus") {
                    reader<StringListAttributeReader> { quote = null;delimiter = ',' }
                    nullable()
                }
                attribute<STRINGLIST>("upsell_skus") {
                    reader<StringListAttributeReader> { quote = null;delimiter = ',' }
                    nullable()
                }
                attribute<STRINGLIST>("additional_images") {
                    reader<StringListAttributeReader> { quote = null;delimiter = ',' }
                    nullable()
                }
                attribute<STRINGLIST>("additional_image_labels") {
                    reader<StringListAttributeReader> { quote = null;delimiter = ',' }
                    nullable()
                }
                attribute<STRINGLIST>("associated_skus") {
                    reader<StringListAttributeReader> { quote = null;delimiter = ',' }
                    nullable()
                }
                attribute<DOUBLE>("ratings_summary") { nullable() }
                attribute<DOUBLE>("cost") { nullable() }
                // todo remove optional in this section
                attribute<STRING>("english_name") { nullable() }
                attribute<STRING>("catalog_sku") { nullable() }
                attribute<STRING>("machine") { nullable() }
                attribute<STRING>("machine_unit") { nullable() }
                attribute<STRING>("machine_vendor") { nullable() }
                // end of todo block
                attribute<DOUBLE>("ts_dimensions_length") { nullable() }
                attribute<DOUBLE>("ts_dimensions_height") { nullable() }
                attribute<DOUBLE>("ts_dimensions_width") { nullable() }
            }
            entity("mage-category", true) {
                attribute<LONG>("entity_type_id") { nullable() }
                attribute<LONG>("attribute_set_id")
                attribute<DATETIME>("created_at") {
                    reader<DateTimeAttributeReader> { pattern = "y-M-d H:m:s" }
                }
                attribute<DATETIME>("updated_at") {
                    reader<DateTimeAttributeReader> { pattern = "y-M-d H:m:s" }
                }
                attribute<LONG>("parent_id")
                attribute<LONG>("increment_id") { nullable() }
                attribute<LONG>("entity_id") { key() }
                attribute<STRING>("children") { nullable() }
                attribute<LONG>("children_count")
                attribute<TEXT>("description") { nullable() }
                attribute<BOOLEAN>("include_in_menu")
                attribute<BOOLEAN>("is_active") { nullable() }
                attribute<BOOLEAN>("is_anchor") { nullable() }
                attribute<BOOLEAN>("is_virtual_category") { nullable() }
                attribute<LONG>("level")
                attribute<LONG>("position")
                attribute<BOOLEAN>("use_name_in_product_search")
                attribute<LONG>("gen_store_id")
                attribute<LONGLIST>("gen_products") {
                    reader<LongListAttributeReader> { quote = null;delimiter = '|' }
                    nullable()
                }
                attribute<STRING>("__path") {
                    builder<CategoryPathFromRelation> {
                        separator = "/"
                        root = "/"
                    }
                }
            }
            entity("mage-customer-group", true) {
                attribute<LONG>("customer_group_id") { key() }
                attribute<LONG>("tax_class_id") { key() }
            }
            entity("mage-adv-price", true) {
                attribute<STRING>("sku") { key() }
                attribute<STRING>("tier_price_website") { key() }
                attribute<STRING>("tier_price_customer_group") { key() }
                attribute<DOUBLE>("tier_price_qty")
                attribute<DOUBLE>("tier_price")
            }
            entity("mage-stock-source", true) {
                attribute<STRING>("source_code") { key() }
                attribute<STRING>("sku") { key() }
                attribute<BOOLEAN>("status")
                attribute<DOUBLE>("quantity")
            }
            entity("onec-group-extended", false) {
                attribute<LONG>("group_code") {
                    reader<OnecGroupToLong>()
                    key()
                }
                attribute<STRING>("group_name") { nullable() }
                attribute<STRING>("magento_path") { nullable() }
                attribute<STRING>("url") { nullable() }
                attribute<TEXT>("description") { nullable() }
            }
        }

        loadEntity("onec-group") {
            fromSources {
                source { file("onec-data"); sheet("Group"); stopOnEmptyRow() }
            }
            rowsToSkip(1)
            keepAbsentForDays(10)

            sourceFields {
                main("group") {
                    field("group_code") {
                        column(0)
                        pattern("^G[0-9]{3,3}$")
                    }
                    field("group_name") { column(1) }
                }
            }
        }
        loadEntity("onec-group-relation") {
            keepAbsentForDays(10)
            fromSources { source { file("onec-data"); sheet("GroupSiteX"); stopOnEmptyRow() } }
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
            fromSources { source { file("onec-data"); sheet("stock"); stopOnEmptyRow() } }
            sourceFields {
                main("product") {
                    field("sku") { column(0); pattern("^[0-9]{4,10}$") } to attribute("sku")
                    field("catalog_sku") { column(1) }
                    field("russian_name") { column(4) }
                    field("english_name") { column(5) }
                    field("manufacturer") { column(10) }
                    field("country_of_manufacture") { column(8) }
                    field("machine_vendor") { column(6) }
                    field("machine") { column(7) }
                    // TODO: 07.01.2019 in production should be open
                    //field("machine_unit") {column(0)}
                    field("weight") { column(9) }
                }
                extra("group") {
                    field("group_code") {
                        column(0)
                        pattern("^G[0-9]{3,3}$")
                    }
                    field("group_name") { column(1) }
                }
            }
        }
        loadEntity("mage-product") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-product"); sheet(Regex(".*")) } }
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
            fromSources { source { file("mage-category"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("category") {}
            }
        }
        loadEntity("mage-customer-group") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-customer-group"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("customer-group") {}
            }
        }
        loadEntity("mage-adv-price") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-adv-price"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("mage-price") {}
            }
        }
        loadEntity("mage-stock-source") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-stock-source"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("stock-source") {}
            }
        }
        loadEntity("onec-group-extended") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("onec-extended-info"); sheet("group-ext-info"); stopOnEmptyRow() } }
            sourceFields {
                main("group-extended") {}
            }
        }
    }

    mediator {
        productionLine {
            output("import-category") {
                inheritFrom("mage-category") /*{
                    exclude("__path")
                }*/
                attribute<BOOLEAN>("remove_flag")
            }
            input {
                entity("onec-group") {
                    statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                    extAttribute<STRING>("transformed_path") {
                        builder<GroupToCategoryPath>()
                    }
                    extAttribute<LONG>("entity_id") {
                        builder { it["group_code"] as? LONG }
                    }
                }
                entity("mage-category") {
                    statuses(EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED)
                }
            }

            pipeline {
                classifier<CategoryClassifier>()
                pipeline(Pipeline.CLASS.MATCHED) {
                    assembler<MatchedCategoryAssembler>()
                }
                pipeline(Pipeline.CLASS.UNMATCHED) {
                    classifier<EntityTypeClassifier> {
                        typeList = listOf("onec-group")
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler<CategoryFromGroupAssembler>()
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier<EntityTypeClassifier> {
                            typeList = listOf("mage-category")
                        }
                        assembler<MarkCategoryToRemove>()
                    }
                }
            }
        }
    }

    printer {
        files {
            val testDataDir = System.getenv("TEST_DATA_DIR")
            file("mage-category") { path("tmp/catalog_category_new.csv") }
        }
        printerLine {
            input {
                entity("import-category") {
                    statuses(EntityStatus.UPDATED)
                }
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
                    assembler { _, _ -> emptyMap() }
                }
                pipeline(Pipeline.CLASS.UNMATCHED) {
                    assembler { _, _ -> emptyMap() }
                }
            }
        }
    }
}
