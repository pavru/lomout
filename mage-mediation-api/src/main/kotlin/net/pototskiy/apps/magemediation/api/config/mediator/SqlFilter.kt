package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.database.schema.SourceEntities
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import net.pototskiy.apps.magemediation.api.plugable.SqlFilterFunction
import net.pototskiy.apps.magemediation.api.plugable.SqlFilterPlugin
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class SqlFilter {
    @PublicApi
    fun where(alias: Alias<SourceEntities>): Op<Boolean> = when (this) {
        is SqlFilterWithPlugin -> pluginClass.createInstance().let {
            it.setOptions(options)
            it.where(alias)
        }
        is SqlFilterWithFunction -> function(alias)
    }
}

class SqlFilterWithPlugin(
    val pluginClass: KClass<out SqlFilterPlugin>,
    val options: NewPlugin.Options = NewPlugin.noOptions
) : SqlFilter()

class SqlFilterWithFunction(
    val function: SqlFilterFunction
) : SqlFilter()
