package net.pototskiy.apps.magemediation.api.config

data class DatabaseServerConfig (
    val host: String,
    val port: Int,
    val user: String,
    val password: String
)