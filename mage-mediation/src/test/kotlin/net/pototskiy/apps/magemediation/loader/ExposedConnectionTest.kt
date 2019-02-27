package net.pototskiy.apps.magemediation.loader

import com.mysql.cj.jdbc.MysqlDataSource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@DisplayName("Exposed connectivity test")
class ExposedConnectionTest {

    object TestTable : IntIdTable("test") {
        val str = varchar("str", 10)
    }

    @Test
    @DisplayName("Connect to DB create and drop table")
    internal fun test() {
        val datasource = MysqlDataSource()
        datasource.setURL("jdbc:mysql://localhost:3306/magemediation")
        datasource.user = "root"
        datasource.password = "root"
        datasource.serverTimezone = TimeZone.getDefault().id
        Database.connect(datasource)
        transaction { SchemaUtils.create(TestTable) }
        transaction { SchemaUtils.drop(TestTable) }
        assertThat(true).isTrue()
    }
}
