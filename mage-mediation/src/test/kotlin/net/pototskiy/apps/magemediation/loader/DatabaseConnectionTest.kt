package net.pototskiy.apps.magemediation.loader

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.sql.DriverManager

@DisplayName("Test database connectivity")
class DatabaseConnectionTest {
    @Test
    @DisplayName("Try to connect to DB and create table test")
    internal fun connectAndCreateTest() {
        val url: String = "jdbc:mysql://localhost:3306/magemediation?useLegacyDatetimeCode=false&serverTimezone=UTC"
        val user: String = "root"
        val password: String = "root"
        DriverManager.getConnection(url, user, password).use { connection ->
            connection.createStatement().use { stmt ->
                stmt.execute("CREATE TABLE test ( id INT(11));")
                assertThat(true).isTrue()
            }
        }
        assertThat(true).isTrue()
    }

    @Test
    @DisplayName("Try to connect to DB and drop table test")
    internal fun connectAnDropTest() {
        val url: String = "jdbc:mysql://localhost:3306/magemediation?useLegacyDatetimeCode=false&serverTimezone=UTC"
        val user: String = "root"
        val password: String = "root"
        DriverManager.getConnection(url, user, password).use { connection ->
            connection.createStatement().use { stmt ->
                stmt.execute("DROP TABLE test;")
                assertThat(true).isTrue()
            }
        }
        assertThat(true).isTrue()
    }
}
