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

@file:Import("entity/OnecGroup.conf.kts")
@file:Import("entity/OnecGroupRelation.conf.kts")
@file:Import("entity/OnecProduct.conf.kts")
@file:Import("entity/MageProduct.conf.kts")
@file:Import("entity/ImportCategory.conf.kts")
@file:Import("entity/MageCategory.conf.kts")
@file:Import("entity/MageCustomerGroup.conf.kts")
@file:Import("entity/MageAdvPrice.conf.kts")
@file:Import("entity/MageStockSource.conf.kts")
@file:Import("entity/OnecGroupExtended.conf.kts")
@file:Import("pipeline/classifier/CategoryClassifier.plugin.conf.kts")
@file:Import("pipeline/assembler/CategoryFromGroupAssembler.plugin.conf.kts")
@file:Import("pipeline/classifier/EntityTypeClassifier.plugin.conf.kts")
@file:Import("pipeline/assembler/MarkCategoryToRemove.plugin.conf.kts")
@file:Import("pipeline/assembler/MatchedCategoryAssembler.plugin.conf.kts")

import CategoryClassifier_plugin_conf.CategoryClassifier
import CategoryFromGroupAssembler_plugin_conf.CategoryFromGroupAssembler
import EntityTypeClassifier_plugin_conf.EntityTypeClassifier
import ImportCategory_conf.ImportCategory
import MageAdvPrice_conf.MageAdvPrice
import MageCategory_conf.MageCategory
import MageCustomerGroup_conf.MageCustomerGroup
import MageProduct_conf.MageProduct
import MageStockSource_conf.MageStockSource
import MarkCategoryToRemove_plugin_conf.MarkCategoryToRemove
import MatchedCategoryAssembler_plugin_conf.MatchedCategoryAssembler
import OnecGroupExtended_conf.OnecGroupExtended
import OnecGroupRelation_conf.OnecGroupRelation
import OnecGroup_conf.OnecGroup
import OnecProduct_conf.OnecProduct
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.emptyDocumentData

config {
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
        loadEntity(OnecGroup::class) {
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
        loadEntity(OnecGroupRelation::class) {
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
        loadEntity(OnecProduct::class) {
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
        loadEntity(MageProduct::class) {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-product"); sheet(Regex(".*")) } }
            sourceFields {
                main("product") {
                    field("additional_attributes")
                }
            }
        }
        loadEntity(MageCategory::class) {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-category"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("category") {}
            }
        }
        loadEntity(MageCustomerGroup::class) {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-customer-group"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("customer-group") {}
            }
        }
        loadEntity(MageAdvPrice::class) {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-adv-price"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("mage-price") {}
            }
        }
        loadEntity(MageStockSource::class) {
            headersRow(0)
            keepAbsentForDays(10)
            fromSources { source { file("mage-stock-source"); sheet(Regex(".*")); stopOnEmptyRow() } }
            sourceFields {
                main("stock-source") {}
            }
        }
        loadEntity(OnecGroupExtended::class) {
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
            output(ImportCategory::class)
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
        printerLine {
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
                    assembler { Document.emptyDocument }
                }
                pipeline(Pipeline.CLASS.UNMATCHED) {
                    assembler { Document.emptyDocument }
                }
            }
        }
    }
}
