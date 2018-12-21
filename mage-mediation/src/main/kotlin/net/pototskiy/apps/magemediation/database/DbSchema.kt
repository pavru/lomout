package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.database.mage.MageProducts
import net.pototskiy.apps.magemediation.database.mage.attribute.*
import net.pototskiy.apps.magemediation.database.onec.OnecGroups
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import net.pototskiy.apps.magemediation.database.onec.attribute.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSchema {
    fun createSchema() {
        transaction {
            // Magento product tables
            SchemaUtils.run {
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
                create(
                    OnecGroups
                )
            }
        }
    }
}