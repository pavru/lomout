@Suppress("unused")
object Versions {
    const val kotlin = "1.3.31"
    const val dokka = "0.9.18"
    const val exposed = "0.14.2"
    val mysql = MysqlVersions(
        connector = "8.0.16"
    )
    const val ivy = "2.5.0-rc1"
    const val guava = "27.1-jre"
    const val jcommander = "1.71"
    const val junit5 = "5.4.1"
    const val assertj = "3.12.2"
    const val log4j = "2.11.2"
    const val slf4j = "1.8.0-beta4"
    const val progressBar = "0.7.2"
    const val jline = "3.9.0"
    const val poi = "4.1.0"
    const val detekt = "1.0.0-RC15"
    const val cache2k = "1.2.2.Final"
}

data class MysqlVersions(
    val connector: String
)
