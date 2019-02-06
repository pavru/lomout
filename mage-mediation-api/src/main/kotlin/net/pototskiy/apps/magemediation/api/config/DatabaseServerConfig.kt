package net.pototskiy.apps.magemediation.api.config

data class DatabaseServerConfig(
    val host: String,
    val port: Int,
    val user: String,
    val password: String
) {
    @ConfigDsl
    class Builder {
        private var host: String = "localhost"
        private var port: Int = 3306
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

        @Suppress("unused")
        fun Builder.port(port: Int) {
            this.port = port
        }

        @Suppress("unused")
        fun Builder.user(user: String) {
            this.user = user
        }

        @Suppress("unused")
        fun Builder.password(password: String) {
            this.password = password
        }

        fun build(): DatabaseServerConfig =
            DatabaseServerConfig(host, port, user, password)


    }
}
