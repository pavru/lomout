/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.pototskiy.apps.lomout.api.config

/**
 * Database configuration element
 *
 * @property server DatabaseServerConfig
 * @property name String
 * @constructor
 */
data class DatabaseConfig(
    val server: DatabaseServerConfig,
    val name: String
) {
    /**
     * Database configuration builder
     *
     * @property name The database configuration name
     * @property server The database server configuration
     * @constructor
     */
    @ConfigDsl
    class Builder(private var name: String = "lomout") {

        private var server: DatabaseServerConfig? = null

        /**
         * Database name
         *
         * ```
         * ...
         *  name("database name")
         * ...
         * ```
         *
         * @param name The database name
         */
        fun name(name: String) {
            this.name = name
        }

        /**
         * Database server configuration
         *
         * ```
         * ...
         *  server {
         *      host("host name")
         *      port(server port number)
         *      user("user name")
         *      password("password")
         *  }
         * ...
         * ```
         * * [host][DatabaseServerConfig.Builder.host] — The database host name, default: *localhost*
         * * [port][DatabaseServerConfig.Builder.port] — The database server port, default: *3306*
         * * [user][DatabaseServerConfig.Builder.user] — The database user name, default: *root*
         * * password — The database user password, default: *root*
         *
         * @see DatabaseServerConfig
         *
         * @param block The database configuration block
         */
        fun server(block: DatabaseServerConfig.Builder.() -> Unit) {
            server = DatabaseServerConfig.Builder().apply(block).build()
        }

        /**
         * Build database configuration
         *
         * @return DatabaseConfig
         */
        fun build(): DatabaseConfig {
            val actualServer = server ?: DatabaseServerConfig.Builder().build()
            return DatabaseConfig(actualServer, name)
        }
    }
}
