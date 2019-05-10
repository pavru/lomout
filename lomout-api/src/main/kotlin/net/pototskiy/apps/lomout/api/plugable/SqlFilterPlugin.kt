package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.database.DbEntityTable
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op

/**
 * Base class for any SQL filters
 */
abstract class SqlFilterPlugin : Plugin() {
    /**
     * Create SQL where part condition
     *
     * @param alias Alias<DbEntityTable> The DB table alias to apply conditions
     * @return Op<Boolean>
     */
    abstract fun where(alias: Alias<DbEntityTable>): Op<Boolean>
}

/**
 * Function type for inline SQL filters
 */
typealias SqlFilterFunction = PluginContextInterface.(Alias<DbEntityTable>) -> Op<Boolean>
