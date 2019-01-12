package net.pototskiy.apps.magemediation.dsl.config

import net.pototskiy.apps.magemediation.api.config.DatabaseConfig
import net.pototskiy.apps.magemediation.api.config.DatabaseServerConfig

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
