package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.PublicApi
import net.pototskiy.apps.magemediation.api.database.schema.SourceEntities
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op

@PublicApi
abstract class SqlFilterPlugin : NewPlugin<Op<Boolean>>() {
    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var alias: Alias<SourceEntities>
    @PublicApi
    fun where(alias: Alias<SourceEntities>): Op<Boolean> {
        this.alias = alias
        return execute()
    }

    class Options : NewPlugin.Options()
}

@PublicApi
typealias SqlFilterFunction = (Alias<SourceEntities>) -> Op<Boolean>
