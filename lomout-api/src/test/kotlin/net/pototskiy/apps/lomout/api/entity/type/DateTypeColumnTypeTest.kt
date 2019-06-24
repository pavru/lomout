package net.pototskiy.apps.lomout.api.entity.type

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.*

@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DateTypeColumnTypeTest {
    private var database: Database? = null
    private val c = DateTypeColumnType().apply { nullable = true }

    @BeforeAll
    internal fun initAll() {
        database = Database.connect(
            "jdbc:mysql://127.0.0.1:3306/test_lomout?serverTimezone=${TimeZone.getDefault().id}",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "root"
        )
    }

    @AfterAll
    internal fun tearDownAll() {
        database = null
    }

    @Test
    fun notNullValueToDBTest() {
        val now = DateTime()
        assertThat(c.notNullValueToDB(DATE(now)))
            .isInstanceOf(java.sql.Date::class.java)
            .isEqualTo(java.sql.Date(now.millis))
        assertThat(c.notNullValueToDB(now))
            .isInstanceOf(java.sql.Date::class.java)
            .isEqualTo(java.sql.Date(now.millis))
    }

    @Test
    fun valueFromDB() {
        val now = DateTime()
        assertThat(c.valueFromDB(DATE(now)))
            .isInstanceOf(DATE::class.java)
            .isEqualTo(DATE(now))
        assertThat(c.valueFromDB(java.sql.Date(now.millis)))
            .isInstanceOf(DATE::class.java)
            .isEqualTo(DATE(now))
    }

    @Test
    fun valueToDB() {
        val now = DateTime()
        assertThat(c.valueToDB(null)).isNull()
        assertThat(c.valueToDB(DATE(now)))
            .isInstanceOf(java.sql.Date::class.java)
            .isEqualTo(java.sql.Date(now.millis))
        assertThat(c.valueToDB(now))
            .isInstanceOf(java.sql.Date::class.java)
            .isEqualTo(java.sql.Date(now.millis))
    }

    @Test
    fun nonNullValueToString() {
        val now = DateTime()
        val nowText = "'${DateTimeFormat.forPattern("YYYY-MM-dd").print(now)}'"
        assertThat(c.nonNullValueToString(DATE(now))).isEqualTo(nowText)
        assertThat(c.nonNullValueToString(now)).isEqualTo(nowText)
    }

    @Test
    fun valueToString() {
        val now = DateTime()
        val nowText = "'${DateTimeFormat.forPattern("YYYY-MM-dd").print(now)}'"
        assertThat(c.valueToString(null)).isEqualTo("NULL")
        assertThat(c.valueToString(DATE(now))).isEqualTo(nowText)
        assertThat(c.valueToString(now)).isEqualTo(nowText)
    }
}