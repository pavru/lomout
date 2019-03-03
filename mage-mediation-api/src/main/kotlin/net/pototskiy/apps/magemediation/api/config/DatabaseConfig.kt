package net.pototskiy.apps.magemediation.api.config

data class DatabaseConfig(
    val server: DatabaseServerConfig,
    val name: String
) {
    @ConfigDsl
    class Builder(private var name: String = "magemediation") {

        private var server: DatabaseServerConfig? = null

        /**
         * Database name
         *
         * @param name The database name
         */
        fun name(name: String) {
            this.name = name
        }

        /**
         * Database server
         */
        fun server(block: DatabaseServerConfig.Builder.() -> Unit) {
            server = DatabaseServerConfig.Builder().apply(block).build()
        }

        fun build(): DatabaseConfig {
            val actualServer = server ?: DatabaseServerConfig.Builder().build()
            return DatabaseConfig(actualServer, name)
        }
    }
}
