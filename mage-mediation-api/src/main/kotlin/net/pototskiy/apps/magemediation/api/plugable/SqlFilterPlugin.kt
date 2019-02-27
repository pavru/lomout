package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op

abstract class SqlFilterPlugin : Plugin() {
    abstract fun where(alias: Alias<DbEntityTable>): Op<Boolean>
}

typealias SqlFilterFunction = (Alias<DbEntityTable>) -> Op<Boolean>
