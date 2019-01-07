package net.pototskiy.apps.magemediation.config.dsl

import net.pototskiy.apps.magemediation.config.newOne.DatabaseServerConfig

@ConfigDsl
class DatabaseServerConfigBuilder {
    /**
     * Database server address, default: localhost
     */
    var host: String = "localhost"
    /**
     * Database server port, default: 3306
     */
    var port: Int = 3306
    /**
     * Database server user login, default: root
     */
    var user: String = "root"
    /**
     * Database server user password, default: root
     */
    var password: String = "root"

    fun build(): DatabaseServerConfig = DatabaseServerConfig(host, port, user, password)
}
