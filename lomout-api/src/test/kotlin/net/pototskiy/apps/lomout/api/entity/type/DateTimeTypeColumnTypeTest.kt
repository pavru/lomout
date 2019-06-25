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
internal class DateTimeTypeColumnTypeTest {
    private var database: Database? = null
    private val c = DateTimeTypeColumnType().apply { nullable = true }

    @BeforeAll
    internal fun initAll() {
        database = Database.connect(
            "jdbc:mysql://127.0.0.1:3306/test_lomout?serverTimezone=${TimeZone.getDefault().id}",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = if (System.getenv("TRAVIS_BUILD_DIR") != null) "" else "root"
        )
    }

    @AfterAll
    internal fun tearDownAll() {
        database = null
    }

    @Test
    fun notNullValueToDBTest() {
        val now = DateTime()
        assertThat(c.notNullValueToDB(DATETIME(now))).isEqualTo(java.sql.Timestamp(now.millis))
        assertThat(now.toDate()).isEqualTo(java.sql.Timestamp(now.millis))
    }

    @Test
    fun valueFromDBTest() {
        val now = DateTime()
        assertThat(c.valueFromDB(DATETIME(now)))
            .isInstanceOf(DATETIME::class.java)
            .isEqualTo(DATETIME(now))
        assertThat(c.valueFromDB(java.sql.Timestamp(now.millis)))
            .isInstanceOf(DATETIME::class.java)
            .isEqualTo(DATETIME(now))
    }

    @Test
    fun valueToDBTest() {
        val now = DateTime()
        assertThat(c.valueToDB(null)).isNull()
        assertThat(c.valueToDB(DATETIME(now)))
            .isInstanceOf(java.sql.Timestamp::class.java)
            .isEqualTo(java.sql.Timestamp(now.millis))
        assertThat(c.valueToDB(now))
            .isInstanceOf(java.sql.Timestamp::class.java)
            .isEqualTo(java.sql.Timestamp(now.millis))
    }

    @Test
    fun nonNullValueToStringTest() {
        val now = DateTime()
        val nowText = "'${DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSSSSS").print(now)}'"
        assertThat(c.nonNullValueToString(DATETIME(now))).isEqualTo(nowText)
        assertThat(c.nonNullValueToString(now)).isEqualTo(nowText)
    }

    @Test
    fun valueToString() {
        val now = DateTime()
        val nowText = "'${DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSSSSS").print(now)}'"
        assertThat(c.valueToString(null)).isEqualTo("NULL")
        assertThat(c.valueToString(DATETIME(now))).isEqualTo(nowText)
        assertThat(c.valueToString(now)).isEqualTo(nowText)
    }
}