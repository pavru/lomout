package net.pototskiy.apps.lomout.api.entity.type

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT
import java.util.*

@Execution(CONCURRENT)
@TestInstance(PER_CLASS)
internal class BooleanTypeColumnTypeTest {

    private val column = BooleanTypeColumnType().apply { nullable = true }
    private var database: Database? = null

    @BeforeAll
    internal fun initAll() {
        database = Database.connect(
            "jdbc:mysql://127.0.0.1:3306/test_lomout?serverTimezone=${TimeZone.getDefault().id}",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                ""
            } else {
                "root"
            }
        )
    }

    @AfterAll
    internal fun tearDownAll() {
        database = null
    }

    @Test
    fun notNullValueToDBTest() {
        assertThat(column.notNullValueToDB(BOOLEAN(true)))
            .isInstanceOf(java.lang.Boolean::class.java)
            .isEqualTo(true)
        assertThat(column.notNullValueToDB(BOOLEAN(false)))
            .isInstanceOf(java.lang.Boolean::class.java)
            .isEqualTo(false)
        assertThat(column.notNullValueToDB(true))
            .isInstanceOf(java.lang.Boolean::class.java)
            .isEqualTo(true)
        assertThat(column.notNullValueToDB(false))
            .isInstanceOf(java.lang.Boolean::class.java)
            .isEqualTo(false)
    }

    @Test
    fun valueFromDBTest() {
        assertThat(column.valueFromDB(BOOLEAN(true)))
            .isInstanceOf(BOOLEAN::class.java)
            .isEqualTo(BOOLEAN(true))
        assertThat(column.valueFromDB(BOOLEAN(false)))
            .isInstanceOf(BOOLEAN::class.java)
            .isEqualTo(BOOLEAN(false))
        assertThat(column.valueFromDB(true))
            .isInstanceOf(BOOLEAN::class.java)
            .isEqualTo(BOOLEAN(true))
        assertThat(column.valueFromDB(false))
            .isInstanceOf(BOOLEAN::class.java)
            .isEqualTo(BOOLEAN(false))
        assertThat(column.valueFromDB(java.lang.Boolean.valueOf(true)))
            .isInstanceOf(BOOLEAN::class.java)
            .isEqualTo(BOOLEAN(true))
        assertThat(column.valueFromDB(java.lang.Boolean.valueOf(false)))
            .isInstanceOf(BOOLEAN::class.java)
            .isEqualTo(BOOLEAN(false))
    }

    @Test
    fun valueToDBTest() {
        assertThat(column.valueToDB(null)).isNull()
        assertThat(column.valueToDB(BOOLEAN(true)))
            .isInstanceOf(java.lang.Boolean::class.java)
            .isEqualTo(true)
        assertThat(column.valueToDB(BOOLEAN(false)))
            .isInstanceOf(java.lang.Boolean::class.java)
            .isEqualTo(false)
        assertThat(column.valueToDB(true))
            .isInstanceOf(java.lang.Boolean::class.java)
            .isEqualTo(true)
        assertThat(column.valueToDB(false))
            .isInstanceOf(java.lang.Boolean::class.java)
            .isEqualTo(false)
    }

    @Test
    fun nonNullValueToStringTest() {
        try {
            database!!.dialect.dataTypeProvider.booleanToStatementString(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val expectedTrue = transaction(database) {
            database!!.dialect.dataTypeProvider.booleanToStatementString(true)
        }
        val expectedFalse = transaction(database) {
            database!!.dialect.dataTypeProvider.booleanToStatementString(false)
        }
        transaction(database) { assertThat(column.nonNullValueToString(BOOLEAN(true))).isEqualTo(expectedTrue) }
        transaction(database) { assertThat(column.nonNullValueToString(BOOLEAN(false))).isEqualTo(expectedFalse) }
        transaction(database) { assertThat(column.nonNullValueToString(true)).isEqualTo(expectedTrue) }
        transaction(database) { assertThat(column.nonNullValueToString(false)).isEqualTo(expectedFalse) }
    }

    @Test
    fun valueToStringTest() {
        val expectedTrue = transaction(database) {
            database!!.dialect.dataTypeProvider.booleanToStatementString(true)
        }
        val expectedFalse = transaction(database) {
            database!!.dialect.dataTypeProvider.booleanToStatementString(false)
        }
        transaction(database) { assertThat(column.valueToString(null)).isEqualTo("NULL") }
        transaction(database) { assertThat(column.valueToString(BOOLEAN(true))).isEqualTo(expectedTrue) }
        transaction(database) { assertThat(column.valueToString(BOOLEAN(false))).isEqualTo(expectedFalse) }
        transaction(database) { assertThat(column.valueToString(true)).isEqualTo(expectedTrue) }
        transaction(database) { assertThat(column.valueToString(false)).isEqualTo(expectedFalse) }
    }
}