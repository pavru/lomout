package net.pototskiy.apps.magemediation.config

data class DatabaseServerConfig (
    val host: String,
    val port: Int,
    val user: String,
    val password: String
)