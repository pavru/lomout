package net.pototskiy.apps.magemediation.config.dsl

import net.pototskiy.apps.magemediation.config.DatabaseConfig
import net.pototskiy.apps.magemediation.config.DatabaseServerConfig

@ConfigDsl
class DatabaseConfigBuilder {
    /**
     * Database name, default: magemediation
     */
    var name: String = "magemediation"
    private var server: DatabaseServerConfig? = null
    /**
     * Configure database server
     */
    @Suppress("unused")
    fun DatabaseConfigBuilder.server(block: DatabaseServerConfigBuilder.() -> Unit) {
        server = DatabaseServerConfigBuilder().apply(block).build()
    }

    fun build(): DatabaseConfig {
        val actualServer = server ?: DatabaseServerConfigBuilder().build()
        return DatabaseConfig(actualServer, name)
    }
}
