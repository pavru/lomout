package net.pototskiy.apps.lomout.api.config

/**
 * Database server configuration
 *
 * @property host String
 * @property port Int
 * @property user String
 * @property password String
 * @constructor
 */
data class DatabaseServerConfig(
    val host: String,
    val port: Int,
    val user: String,
    val password: String
) {
    /**
     * Database server configuration builder class
     *
     * @property host String
     * @property port Int
     * @property user String
     * @property password String
     */
    @ConfigDsl
    class Builder {
        private var host: String = "localhost"
        private var port: Int = defaultPort
        private var user: String = "root"
        private var password: String = "root"

        /**
         * Database host address
         *
         * @param host The database host address, default: *localhost*
         */
        @Suppress("unused")
        fun Builder.host(host: String) {
            this.host = host
        }

        /**
         * Database server port
         *
         * @receiver Builder
         * @param port Int The server port, default: *3306*
         */
        @Suppress("unused")
        fun Builder.port(port: Int) {
            this.port = port
        }

        /**
         * Database server user name
         *
         * @receiver Builder
         * @param user String The user name, default: *root*
         */
        @Suppress("unused")
        fun Builder.user(user: String) {
            this.user = user
        }

        /**
         * Database user password
         *
         * @receiver Builder
         * @param password String The user password, default: *root*
         */
        @Suppress("unused")
        fun Builder.password(password: String) {
            this.password = password
        }

        /**
         * Build database configuration
         *
         * @return DatabaseServerConfig
         */
        fun build(): DatabaseServerConfig =
            DatabaseServerConfig(host, port, user, password)

        /**
         * Companion object
         */
        companion object {
            /**
             * Default database server port
             */
            const val defaultPort = 3306
        }
    }
}
