package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.config.dataset.EmptyRowAction
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import net.pototskiy.apps.magemediation.database.source.SourceDataStatus
import net.pototskiy.apps.magemediation.source.excel.ExcelWorkbook
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertEquals

object DataLoadingFeature : Spek({
    Feature("Loading data from source xls, xlsx, csv data files") {
        Scenario("Load 6 products to one c product empty table") {
            val util = LoadingDataTestPrepare()
            val config = util.loadConfiguration()
            util.initDataBase()

            val hssfWorkbook = util.openHSSWorkbookFromResources("test.onec.xls")
            val workbook = ExcelWorkbook(hssfWorkbook)
            val loader = LoaderFactory.create(LoadDestination.ONEC_PRODUCT)

            When("clean onec product table") {
                cleanUpProductTable()
            }
            Then("it should not have any record") {
                assertEquals(0, transaction { OnecProducts.selectAll().count() })
            }
            When("load product data to database") {
                val dataset = config.datasets.find { it.name == "onec-test-product" }!!
                loader.load(
                    workbook["test-stock"],
                    dataset,
                    dataset.sources[0].emptyRowAction
                )
            }
            Then("it should be 6 products are in the table") {
                assertEquals(6, transaction { OnecProducts.selectAll().count() })
            }
            Then("it should be 6 products with status CREATED, CREATED") {
                assertEquals(
                    6,
                    transaction {
                        OnecProducts
                            .select {
                                ((OnecProducts.currentStatus eq SourceDataStatus.CREATED.name)
                                        and (OnecProducts.previousStatus eq SourceDataStatus.CREATED.name))
                            }
                            .count()
                    }
                )
            }
            When("repeat loading of product date without one of them") {
                val dataset = config.datasets.find { it.name == "onec-test-product" }!!
                val row = hssfWorkbook.getSheet("test-stock").getRow(5)
                hssfWorkbook
                    .getSheet("test-stock")
                    .removeRow(row)
                loader.load(ExcelWorkbook(hssfWorkbook)["test-stock"], dataset, EmptyRowAction.IGNORE)
            }
            Then("it should be 6 products are in the table") {
                assertEquals(6, transaction { OnecProducts.selectAll().count() })
            }
            Then("it should be 5 products with status CREATED, UNCHANGED") {
                assertEquals(
                    5,
                    transaction {
                        OnecProducts
                            .select {
                                ((OnecProducts.currentStatus eq SourceDataStatus.UNCHANGED.name)
                                        and (OnecProducts.previousStatus eq SourceDataStatus.CREATED.name))
                            }
                            .count()
                    }
                )
            }
            Then("should be 1 product with status CREATED, REMOVED") {
                assertEquals(
                    1,
                    transaction {
                        OnecProducts
                            .select {
                                ((OnecProducts.currentStatus eq SourceDataStatus.REMOVED.name)
                                        and (OnecProducts.previousStatus eq SourceDataStatus.CREATED.name))
                            }
                            .count()
                    }
                )
            }
        }

    }
})

fun cleanUpProductTable() {
    transaction { OnecProducts.deleteAll() }
}
