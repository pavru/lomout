package database

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DbSchema {
    fun createSchema() {
        transaction {
            SchemaUtils.create(OneGroupTable)
            SchemaUtils.create(OneProductTable)
        }
    }
}