package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.PublicApi
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import net.pototskiy.apps.lomout.api.plugable.SqlFilterFunction
import net.pototskiy.apps.lomout.api.plugable.SqlFilterPlugin
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract SQL filter for pipeline input entities
 */
sealed class SqlFilter {
    /**
     * Create SQL where clause
     *
     * @param alias Alias<DbEntityTable> Table alias
     * @return Op<Boolean>
     */
    @PublicApi
    fun where(alias: Alias<DbEntityTable>): Op<Boolean> = when (this) {
        is SqlFilterWithPlugin -> pluginClass.createInstance().let {
            it.apply(options)
            it.where(alias)
        }
        is SqlFilterWithFunction -> PluginContext.function(alias)
    }
}

/**
 * Pipeline input SQL filer with a plugin
 *
 * @property pluginClass KClass<out SqlFilterPlugin> Filter plugin class
 * @property options SqlFilterPlugin.() The filter options
 * @constructor
 */
class SqlFilterWithPlugin(
    val pluginClass: KClass<out SqlFilterPlugin>,
    val options: SqlFilterPlugin.() -> Unit = {}
) : SqlFilter()

/**
 * Pipeline input SQL filter with function
 *
 * @property function SqlFilterFunction The filter generation function
 * @constructor
 */
class SqlFilterWithFunction(
    val function: SqlFilterFunction
) : SqlFilter()
