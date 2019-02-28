@file:Import("reader/OnecGroupToLong.plugin.conf.kts")
@file:Import("builder/GroupPathFromRelation.plugin.conf.kts")
@file:Import("builder/CategoryPathFromRelation.plugin.conf.kts")
@file:Import("builder/RelationGroupNameFromGroup.plugin.conf.kts")
@file:Import("transformer/GroupAddLeadingG.plugin.conf.kts")
@file:Import("processor/MatchedCategoryProcessor.plugin.conf.kts")
@file:Import("processor/UnMatchedCategoryProcessor.plugin.conf.kts")
@file:Import("processor/UnMatchedGroupProcessor.plugin.conf.kts")
@file:Import("reader/GroupToCategoryPath.plugin.conf.kts")
@file:Import("pipeline/classifier/CategoryClassifier.plugin.conf.kts")
@file:Import("pipeline/assembler/MatchedCategoryAssembler.plugin.conf.kts")
@file:Import("pipeline/classifier/EntityTypeClassifier.plugin.conf.kts")
@file:Import("pipeline/assembler/CategoryFromGroupAssembler.plugin.conf.kts")
@file:Import("pipeline/assembler/MarkCategoryToRemove.plugin.conf.kts")

config {
    database {
        name("magemediation")
        server {
            host("localhost")
            port(3306)
            user("root")
            if (System.getProperty("os.name").toLowerCase().contains("linux"))
            {
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
                attribute<LongType>("group_code") {
                    key()
                    reader<OnecGroupToLong>()
                }
                attribute<StringType>("group_name")
                attribute<StringType>("__path") {
                    builder<GroupPathFromRelation> {
                        separator = "/"
                        root = "/Root Catalog/Default Category/Каталог/"
                    }
                }
            }
            entity("onec-group-relation", false) {
                attribute<LongType>("group_code") { key() }
                attribute<LongType>("group_parent_code") { nullable() }
                attribute<StringType>("group_name") {
                    builder<RelationGroupNameFromGroup>()
                }
            }
            entity("onec-product", true) {
                attribute<LongType>("sku") { key() }
                attribute<DoubleType>("weight")
                attribute<LongType>("group_code") {
                    reader<OnecGroupToLong>()
                }
            }
            entity("mage-product", true) {
                attribute<StringType>("sku") { key() }
                attribute<AttributeListType>("additional_attributes") {
                    reader<AttributeListReader> {
                        quote = "";delimiter = ",";valueQuote = "\"";valueDelimiter = "="
                    }
                }
                attribute<TextType>("description")
                attribute<TextType>("short_description") { nullable() }
                attribute<DoubleType>("weight") { nullable() }
                attribute<BooleanType>("product_online")
                attribute<DoubleType>("price")
                attribute<DoubleType>("special_price") { nullable() }
                attribute<DateType>("special_price_from_date") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                    nullable()
                }
                attribute<DateType>("special_price_to_date") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                    nullable()
                }
                attribute<DateTimeType>("created_at") {
                    reader<DateTimeAttributeReader> { pattern = "d.M.yy, H:m" }
                }
                attribute<DateTimeType>("updated_at") {
                    reader<DateTimeAttributeReader> { pattern = "d.M.yy, H:m" }
                }
                attribute<DateType>("new_from_date") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                    nullable()
                }
                attribute<DateType>("new_to_date") {
                    reader<DateAttributeReader> { pattern = "d.M.yy" }
                    nullable()
                }
                attribute<DoubleType>("qty")
                attribute<DoubleType>("out_of_stock_qty")
                attribute<BooleanType>("use_config_min_qty")
                attribute<BooleanType>("is_qty_decimal")
                attribute<BooleanType>("allow_backorders")
                attribute<BooleanType>("use_config_backorders")
                attribute<DoubleType>("min_cart_qty")
                attribute<BooleanType>("use_config_min_sale_qty")
                attribute<DoubleType>("max_cart_qty")
                attribute<BooleanType>("use_config_max_sale_qty")
                attribute<BooleanType>("is_in_stock")
                attribute<DoubleType>("notify_on_stock_below")
                attribute<BooleanType>("use_config_notify_stock_qty")
                attribute<BooleanType>("manage_stock")
                attribute<BooleanType>("use_config_manage_stock")
                attribute<BooleanType>("use_config_qty_increments")
                attribute<DoubleType>("qty_increments")
                attribute<BooleanType>("use_config_enable_qty_inc")
                attribute<BooleanType>("enable_qty_increments")
                attribute<BooleanType>("is_decimal_divided")
                attribute<LongType>("website_id")
                attribute<StringListType>("related_skus") {
                    reader<StringListAttributeReader> { quote = "";delimiter = "," }
                    nullable()
                }
                attribute<StringListType>("crosssell_skus") {
                    reader<StringListAttributeReader> { quote = "";delimiter = "," }
                    nullable()
                }
                attribute<StringListType>("upsell_skus") {
                    reader<StringListAttributeReader> { quote = "";delimiter = "," }
                    nullable()
                }
                attribute<StringListType>("additional_images") {
                    reader<StringListAttributeReader> { quote = "";delimiter = "," }
                    nullable()
                }
                attribute<StringListType>("additional_image_labels") {
                    reader<StringListAttributeReader> { quote = "";delimiter = "," }
                    nullable()
                }
                attribute<StringListType>("associated_skus") {
                    reader<StringListAttributeReader> { quote = "";delimiter = "," }
                    nullable()
                }
                attribute<DoubleType>("ratings_summary") { nullable() }
                attribute<DoubleType>("cost") { nullable() }
                // todo remove optional in this section
                attribute<StringType>("english_name") { nullable() }
                attribute<StringType>("catalog_sku") { nullable() }
                attribute<StringType>("machine") { nullable() }
                attribute<StringType>("machine_unit") { nullable() }
                attribute<StringType>("machine_vendor") { nullable() }
                // end of todo block
                attribute<DoubleType>("ts_dimensions_length") { nullable() }
                attribute<DoubleType>("ts_dimensions_height") { nullable() }
                attribute<DoubleType>("ts_dimensions_width") { nullable() }
            }
            entity("mage-category", true) {
                attribute<LongType>("entity_type_id") { nullable() }
                attribute<LongType>("attribute_set_id")
                attribute<DateTimeType>("created_at") {
                    reader<DateTimeAttributeReader> { pattern = "y-M-d H:m:s" }
                }
                attribute<DateTimeType>("updated_at") {
                    reader<DateTimeAttributeReader> { pattern = "y-M-d H:m:s" }
                }
                attribute<LongType>("parent_id")
                attribute<LongType>("increment_id") { nullable() }
                attribute<LongType>("entity_id") { key() }
                attribute<StringType>("children") { nullable() }
                attribute<LongType>("children_count")
                attribute<TextType>("description") { nullable() }
                attribute<BooleanType>("include_in_menu")
                attribute<BooleanType>("is_active") { nullable() }
                attribute<BooleanType>("is_anchor") { nullable() }
                attribute<BooleanType>("is_virtual_category") { nullable() }
                attribute<LongType>("level")
                attribute<LongType>("position")
                attribute<BooleanType>("use_name_in_product_search")
                attribute<LongType>("gen_store_id")
                attribute<LongListType>("gen_products") {
                    reader<LongListAttributeReader> { quote = "";delimiter = "|" }
                    nullable()
                }
                attribute<StringType>("__path") {
                    builder<CategoryPathFromRelation> {
                        separator = "/"
                        root = "/"
                    }
                }
            }
            entity("mage-customer-group", true) {
                attribute<LongType>("customer_group_id") { key() }
                attribute<LongType>("tax_class_id") { key() }
            }
            entity("mage-adv-price", true) {
                attribute<StringType>("sku") { key() }
                attribute<StringType>("tier_price_website") { key() }
                attribute<StringType>("tier_price_customer_group") { key() }
                attribute<DoubleType>("tier_price_qty")
                attribute<DoubleType>("tier_price")
            }
            entity("mage-stock-source", true) {
                attribute<StringType>("source_code") { key() }
                attribute<StringType>("sku") { key() }
                attribute<BooleanType>("status")
                attribute<DoubleType>("quantity")
            }
            entity("onec-group-extended", false) {
                attribute<LongType>("group_code") {
                    reader<OnecGroupToLong>()
                    key()
                }
                attribute<StringType>("group_name") { nullable() }
                attribute<StringType>("magento_path") { nullable() }
                attribute<StringType>("url") { nullable() }
                attribute<TextType>("description") { nullable() }
            }
        }

        loadEntity("onec-group") {
            fromSources {
                source { file("onec-data").sheet("Group").stopOnEmptyRow() }
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
            fromSources { source { file("onec-data").sheet("GroupSiteX").stopOnEmptyRow() } }
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
            fromSources { source { file("onec-data").sheet("stock").stopOnEmptyRow() } }
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
                    //field("machine_unit") { column(0) }
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
        loadEntity("onec-group-extended") {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("onec-extended-info").sheet("group-ext-info").stopOnEmptyRow() } }
            sourceFields {
                main("group-extended") {}
            }
        }
    }

    mediator {
        crossProductionLine {
            output("import-category") {
                inheritFrom("mage-category") /*{
                    exclude("__path")
                }*/
                attribute<BooleanType>("remove_flag")
            }
            input {
                entity("onec-group") {
                    filter {
                        with(DbEntityTable) {
                            it[currentStatus] neq EntityStatus.REMOVED
                        }
                    }
                    extAttribute<StringType>("transformed_path", "group_code") {
                        reader<GroupToCategoryPath>()
                    }
                    extAttribute<LongType>("entity_id", "group_code")
                }
                entity("mage-category") {
                    filter {
                        with(DbEntityTable) {
                            it[currentStatus] neq EntityStatus.REMOVED
                        }
                    }
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
}
