package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.database.mage.*
import net.pototskiy.apps.magemediation.database.mage.attribute.*
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import net.pototskiy.apps.magemediation.database.mediation.category.attribute.*
import net.pototskiy.apps.magemediation.database.onec.OnecGroupRelations
import net.pototskiy.apps.magemediation.database.onec.OnecGroups
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import net.pototskiy.apps.magemediation.database.onec.attribute.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSchema {
    fun createSchema() {
        transaction {
            SchemaUtils.run {
                // Magento product tables
                create(
                    MageProducts,
                    MageProductBools,
                    MageProductVarchars,
                    MageProductTexts,
                    MageProductInts,
                    MageProductDoubles,
                    MageProductDatetimes,
                    MageProductDates
                )
                // Magento medium tables
                create(
                    MageCategories,
                    MageCatBools,
                    MageCatVarchars,
                    MageCatTexts,
                    MageCatInts,
                    MageCatDoubles,
                    MageCatDatetimes,
                    MageCatDates
                )
                // OneC product tables
                create(
                    OnecProducts,
                    OnecProductBools,
                    OnecProductVarchars,
                    OnecProductTexts,
                    OnecProductInts,
                    OnecProductDoubles,
                    OnecProductDatetimes,
                    OnecProductDates
                )
                // OneC group tables
                create(OnecGroups, OnecGroupRelations)
                // Magento customer group tables
                create(MageCustomerGroups)
                // Magento advanced price
                create(MageAdvPrices)
                // Magento stock source tables
                create(MageStockSources)
                // Magento medium tables
                create(
                    MediumCategories,
                    MCategoryBoolTable,
                    MCategoryVarcharTable,
                    MCategoryTextTable,
                    MCategoryIntTable,
                    MCategoryDoubleTable,
                    MCategoryDateTimeTable,
                    MCategoryDateTable
                )
            }
        }
    }
}
