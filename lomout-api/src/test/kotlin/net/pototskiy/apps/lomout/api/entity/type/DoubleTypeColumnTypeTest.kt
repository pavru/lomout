package net.pototskiy.apps.lomout.api.entity.type

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.*

@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DoubleTypeColumnTypeTest {

    private val c = DoubleTypeColumnType().apply { nullable = true }
    private var database: Database? = null

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
        transaction {
            assertThat(c.notNullValueToDB(DOUBLE(1.2))).isEqualTo(1.2)
            assertThat(c.notNullValueToDB(1.2)).isEqualTo(1.2)
        }
    }

    @Test
    fun valueFromDBTest() {
        transaction {
            assertThat(c.valueFromDB(1.2))
                .isInstanceOf(DOUBLE::class.java)
                .isEqualTo(DOUBLE(1.2))
            assertThat(c.valueFromDB(DOUBLE(1.2)))
                .isInstanceOf(DOUBLE::class.java)
                .isEqualTo(DOUBLE(1.2))
            assertThatThrownBy { c.valueFromDB("1.2") }
                .isInstanceOf(ClassCastException::class.java)
        }
    }

    @Test
    fun valueToDBTest() {
        transaction {
            assertThat(c.valueToDB(null)).isNull()
            assertThat(c.valueToDB(DOUBLE(1.2))).isEqualTo(1.2)
            assertThat(c.valueToDB(1.2)).isEqualTo(1.2)
        }
    }

    @Test
    fun nonNullValueToStringTest() {
        transaction {
            assertThat(c.nonNullValueToString(DOUBLE(1.2))).isEqualTo("1.2")
            assertThat(c.nonNullValueToString(1.2)).isEqualTo("1.2")
        }
    }

    @Test
    fun valueToStringTest() {
        transaction {
            assertThat(c.valueToString(null)).isEqualTo("NULL")
            assertThat(c.valueToString(DOUBLE(1.2))).isEqualTo("1.2")
            assertThat(c.valueToString(1.2)).isEqualTo("1.2")
        }
    }
}