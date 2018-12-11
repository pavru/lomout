package configuration

import com.fasterxml.jackson.annotation.JsonProperty

data class DatabaseServerDto(val host: String = "localhost", val port: Int = 3306)
data class DatabaseDto(
    val server: DatabaseServerDto,
    @field:JsonProperty("db-name")
    val name: String = "oooast",
    val user: String = "root",
    val password: String = "root"
)