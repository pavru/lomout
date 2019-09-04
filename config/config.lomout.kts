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

@file:Import("entity/OnecGroup.lomout.kts")
@file:Import("entity/OnecGroupRelation.lomout.kts")
@file:Import("entity/OnecProduct.lomout.kts")
@file:Import("entity/MageProduct.lomout.kts")
@file:Import("entity/ImportCategory.lomout.kts")
@file:Import("entity/MageCategory.lomout.kts")
@file:Import("entity/MageCustomerGroup.lomout.kts")
@file:Import("entity/MageAdvPrice.lomout.kts")
@file:Import("entity/MageStockSource.lomout.kts")
@file:Import("entity/OnecGroupExtended.lomout.kts")
@file:Import("pipeline/classifier/CategoryClassifier.plugin.lomout.kts")
@file:Import("pipeline/assembler/CategoryFromGroupAssembler.plugin.lomout.kts")
@file:Import("pipeline/classifier/EntityTypeClassifier.plugin.lomout.kts")
@file:Import("pipeline/assembler/MarkCategoryToRemove.plugin.lomout.kts")
@file:Import("pipeline/assembler/MatchedCategoryAssembler.plugin.lomout.kts")

import CategoryClassifier_plugin_lomout.CategoryClassifier
import CategoryFromGroupAssembler_plugin_lomout.CategoryFromGroupAssembler
import EntityTypeClassifier_plugin_lomout.EntityTypeClassifier
import ImportCategory_lomout.ImportCategory
import MageAdvPrice_lomout.MageAdvPrice
import MageCategory_lomout.MageCategory
import MageCustomerGroup_lomout.MageCustomerGroup
import MageProduct_lomout.MageProduct
import MageStockSource_lomout.MageStockSource
import MarkCategoryToRemove_plugin_lomout.MarkCategoryToRemove
import MatchedCategoryAssembler_plugin_lomout.MatchedCategoryAssembler
import OnecGroupExtended_lomout.OnecGroupExtended
import OnecGroupRelation_lomout.OnecGroupRelation
import OnecGroup_lomout.OnecGroup
import OnecProduct_lomout.OnecProduct
import net.pototskiy.apps.lomout.api.script.mediator.Pipeline
import net.pototskiy.apps.lomout.api.script.script
import org.jetbrains.kotlin.script.util.Import

script {
    database {
        name("lomout_test")
        server {
            host("localhost")
            port(27017)
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
        load<OnecGroup> {
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
        load<OnecGroupRelation> {
            keepAbsentForDays(10)
            fromSources { source { file("onec-data"); sheet("GroupSiteX"); stopOnEmptyRow() } }
            sourceFields {
                main("group-relation") {
                    field("group_code") { column(0) } to attribute("group_code")
                    field("group_parent_code") { column(1) }
//                    field("group_name") { column(2) }
                }
            }
        }
        load<OnecProduct> {
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
        load<MageProduct> {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-product"); sheet(Regex(".*")) } }
            sourceFields {
                main("product") {
                    field("additional_attributes")
                }
            }
        }
        load<MageCategory> {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-category"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("category") {}
            }
        }
        load<MageCustomerGroup> {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-customer-group"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("customer-group") {}
            }
        }
        load<MageAdvPrice> {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-adv-price"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("mage-price") {}
            }
        }
        load<MageStockSource> {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-stock-source"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("stock-source") {}
            }
        }
        load<OnecGroupExtended> {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("onec-extended-info"); sheet("group-ext-info"); stopOnEmptyRow() } }
            sourceFields {
                main("group-extended") {}
            }
        }
    }

    mediator {
        produce<ImportCategory> {
            input {
                entity(OnecGroup::class)
                entity(MageCategory::class)
            }

            pipeline {
                classifier<CategoryClassifier>()
                pipeline(Pipeline.CLASS.MATCHED) {
                    assembler<MatchedCategoryAssembler>()
                }
                pipeline(Pipeline.CLASS.UNMATCHED) {
                    classifier<EntityTypeClassifier> {
                        typeList = listOf(OnecGroup::class)
                    }
                    pipeline(Pipeline.CLASS.MATCHED) {
                        assembler<CategoryFromGroupAssembler>()
                    }
                    pipeline(Pipeline.CLASS.UNMATCHED) {
                        classifier<EntityTypeClassifier> {
                            typeList = listOf(MageCategory::class)
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
        print<ImportCategory> {
            input {
                entity(ImportCategory::class)
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
                    assembler { null }
                }
                pipeline(Pipeline.CLASS.UNMATCHED) {
                    assembler { null }
                }
            }
        }
    }
}
