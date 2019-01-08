package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.config.EmptyRowAction
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import net.pototskiy.apps.magemediation.database.onec.attribute.*
import net.pototskiy.apps.magemediation.source.WorkbookFactory
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.format.DateTimeFormat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertEquals

object DataLoadingAttributesFeature : Spek({
    Feature("Loading data from source files with attributes") {
        val util = LoadingDataTestPrepare()
        val config = util.loadConfiguration()
        util.initDataBase()
        transaction { OnecProducts.deleteAll() }
        listOf(
            SourceTestSet("test.onec.xls", "onec-test-product", "test-stock"),
            SourceTestSet("test.headers.csv", "headers-test-product", "default")
        ).forEach { testData ->
            Scenario("load data from ${testData.file} with attributes") {
                val loader = LoaderFactory.create(LoadDestination.ONEC_PRODUCT)
                When("load product from data file") {
                    val workbook =
                        WorkbookFactory.create(this::class.java.classLoader.getResource(testData.file))//ExcelWorkbook(hssfWorkbook)
                    loader.load(
                        workbook[testData.sheet],
                        config.loader.datasets.findLast { it.name == testData.dataset }!!,
                        EmptyRowAction.IGNORE
                    )
                }
                Then("it should be 6 products in the database") {
                    assertEquals(
                        6,
                        transaction { OnecProducts.selectAll().count() }
                    )
                }
                Then("product 1,2,3 have group G001/G001") {
                    assertEquals(
                        6,
                        transaction {
                            (OnecProducts innerJoin OnecProductVarchars)
                                .slice(OnecProductVarchars.code)
                                .select {
                                    ((OnecProducts.sku inList listOf("1", "2", "3"))
                                            and (OnecProductVarchars.code inList listOf("group_code", "group_name"))
                                            and (OnecProductVarchars.value eq "G001"))
                                }.count()
                        }
                    )
                }
                Then("product 4,5,6 have group G002/G002") {
                    assertEquals(
                        6,
                        transaction {
                            (OnecProducts innerJoin OnecProductVarchars)
                                .slice(OnecProductVarchars.code)
                                .select {
                                    ((OnecProducts.sku inList listOf("4", "5", "6"))
                                            and (OnecProductVarchars.code inList listOf("group_code", "group_name"))
                                            and (OnecProductVarchars.value eq "G002"))
                                }.count()
                        }
                    )
                }
                Then("products have description like 'description'+sku") {
                    assertEquals(
                        6,
                        transaction {
                            (OnecProducts innerJoin OnecProductTexts)
                                .slice(OnecProductTexts.code)
                                .select {
                                    ((OnecProductTexts.code eq "description")
                                            and (OnecProductTexts.value eq ("description" concat OnecProducts.sku)))
                                }.count()
                        }
                    )
                }
                Then("products have attribute bool_val with correct values") {
                    assertEquals(
                        6,
                        transaction {
                            (OnecProducts innerJoin OnecProductBools)
                                .slice(OnecProductBools.value)
                                .select {
                                    ((OnecProductBools.code eq "bool_val")
                                            and (((OnecProducts.sku inList listOf(
                                        "1",
                                        "2",
                                        "3"
                                    )) and (OnecProductBools.value eq true))
                                            or ((OnecProducts.sku inList listOf(
                                        "4",
                                        "5",
                                        "6"
                                    )) and (OnecProductBools.value eq false))))
                                }.count()
                        }
                    )
                }
                Then("products have attribute date_val with correct values") {
                    for (i in 1..6) {
                        val date = DateTimeFormat.forPattern("d.M.yy").parseDateTime("${i + 6}.${i + 6}.${i + 2006}")
                        assertEquals(
                            1,
                            transaction {
                                (OnecProducts innerJoin OnecProductDates)
                                    .slice(OnecProductDates.code)
                                    .select {
                                        ((OnecProducts.sku eq "$i")
                                                and (OnecProductDates.code eq "date_val")
                                                and (OnecProductDates.value eq date))
                                    }.count()
                            }
                        )
                    }
                }
                Then("products have attribute datetime_val with correct values") {
                    for (i in 1..6) {
                        val datetime = DateTimeFormat.forPattern("d.M.yy H:m")
                            .parseDateTime("${i + 6}.${i + 6}.${i + 2006} ${i + 6}:${i + 6}")
                        assertEquals(
                            1,
                            transaction {
                                (OnecProducts innerJoin OnecProductDatetimes)
                                    .slice(OnecProductDatetimes.code)
                                    .select {
                                        ((OnecProducts.sku eq "$i")
                                                and (OnecProductDatetimes.code eq "datetime_val")
                                                and (OnecProductDatetimes.value eq datetime))
                                    }.count()
                            }
                        )
                    }
                }
                Then("products have attribute string_list with correct values") {
                    for (i in 1..6) {
                        val valInDB = transaction {
                            (OnecProducts innerJoin OnecProductVarchars)
                                .slice(OnecProductVarchars.value)
                                .select {
                                    ((OnecProducts.sku eq "$i")
                                            and (OnecProductVarchars.code eq "string_list"))
                                }
                                .orderBy(OnecProductVarchars.index)
                                .toList()
                                .map { it[OnecProductVarchars.value] }
                        }
                        valInDB shouldContainAll (i..i + 2).toList().map { "val$i" }
                    }
                }
                Then("products have attribute bool_list with correct values") {
                    for (i in 1..6) {
                        val valInDB = transaction {
                            (OnecProducts innerJoin OnecProductBools)
                                .slice(OnecProductBools.value)
                                .select {
                                    ((OnecProducts.sku eq "$i")
                                            and (OnecProductBools.code eq "bool_list"))
                                }
                                .orderBy(OnecProductBools.index)
                                .toList()
                                .map { it[OnecProductBools.value] }
                        }
                        val expected = (0..2).toList().map { (i - 1) and (4 shr it) != 0 }
                        valInDB shouldContainAll expected
                    }
                }
                Then("products have attribute int_list with correct values") {
                    for (i in 1..6) {
                        val actual = transaction {
                            (OnecProducts innerJoin OnecProductInts)
                                .slice(OnecProductInts.value)
                                .select {
                                    ((OnecProducts.sku eq "$i")
                                            and (OnecProductInts.code eq "int_list"))
                                }
                                .orderBy(OnecProductInts.index)
                                .toList()
                                .map { it[OnecProductInts.value] }
                        }
                        val expected = (10..12).toList().map { (it + i).toLong() }
                        actual shouldContainAll expected
                    }
                }
                Then("products have attribute double_list correct values") {
                    for (i in 1..6) {
                        val actual = transaction {
                            (OnecProducts innerJoin OnecProductDoubles)
                                .slice(OnecProductDoubles.value)
                                .select {
                                    ((OnecProducts.sku eq "$i")
                                            and (OnecProductDoubles.code eq "double_list"))
                                }
                                .orderBy(OnecProductDoubles.index)
                                .toList()
                                .map { it[OnecProductDoubles.value] }
                        }
                        val expected = (10..12).toList().map { (it + i).toDouble() + (it + i).toDouble() / 100.0 }
                        actual shouldContainAll expected
                    }
                }
                Then("products have attribute date_list with correct values") {
                    for (i in 1..6) {
                        val actual = transaction {
                            (OnecProducts innerJoin OnecProductDates)
                                .slice(OnecProductDates.value)
                                .select {
                                    ((OnecProducts.sku eq "$i")
                                            and (OnecProductDates.code eq "date_list"))
                                }
                                .orderBy(OnecProductDates.index)
                                .toList()
                                .map { it[OnecProductDates.value] }
                        }
                        val formatter = DateTimeFormat.forPattern("d.M.yy")
                        val expected = (i + 6..i + 7)
                            .toList()
                            .mapIndexed { j, v ->
                                formatter.parseDateTime("$v.${j % 2 + 11}.${j % 2 + 11}")
                            }
                        actual shouldContainAll expected
                    }
                }
                Then("products have attribute datetime_list with correct values") {
                    for (i in 1..6) {
                        val actual = transaction {
                            (OnecProducts innerJoin OnecProductDatetimes)
                                .slice(OnecProductDatetimes.value)
                                .select {
                                    ((OnecProducts.sku eq "$i")
                                            and (OnecProductDatetimes.code eq "datetime_list"))
                                }
                                .orderBy(OnecProductDatetimes.index)
                                .toList()
                                .map { it[OnecProductDatetimes.value] }
                        }
                        val formatter = DateTimeFormat.forPattern("d.M.yy H:m")
                        val expected = (i + 6..i + 7)
                            .toList()
                            .mapIndexed { j, v ->
                                formatter.parseDateTime("$v.${j % 2 + 11}.${j % 2 + 11} $v:${j % 2 + 11}")
                            }
                        actual shouldContainAll expected
                    }
                }
                Then("products have attribute nested1 with correct values") {
                    for (i in 1..6) {
                        val actual = transaction {
                            (OnecProducts innerJoin OnecProductInts)
                                .slice(OnecProductInts.value)
                                .select {
                                    ((OnecProducts.sku eq "$i")
                                            and (OnecProductInts.code eq "nested1"))
                                }
                                .orderBy(OnecProductInts.index)
                                .toList()
                                .map { it[OnecProductInts.value] }
                        }
                        actual.count() shouldEqual 1
                        actual.first() shouldEqual (i + 10).toLong()
                    }
                }
                Then("products have attribute nested2 with correct values") {
                    for (i in 1..6) {
                        val actual = transaction {
                            (OnecProducts innerJoin OnecProductInts)
                                .slice(OnecProductInts.value)
                                .select {
                                    ((OnecProducts.sku eq "$i")
                                            and (OnecProductInts.code eq "nested2"))
                                }
                                .orderBy(OnecProductInts.index)
                                .toList()
                                .map { it[OnecProductInts.value] }
                        }
                        actual.count() shouldEqual 1
                        actual.first() shouldEqual (i + 11).toLong()
                    }
                }
            }
        }
    }
})

infix fun String.concat(other: Column<*>) = object : Function<String>(VarCharColumnType()) {
    override fun toSQL(queryBuilder: QueryBuilder): String {
        return "CONCAT('${this@concat}',${other.toSQL(queryBuilder)})"
    }
}
