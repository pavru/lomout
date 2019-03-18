@Suppress("unused")
object Versions {
    const val kotlin = "1.3.21"
    const val dokka = "0.9.17"
    const val exposed = "0.13.3"
    val mysql = MysqlVersions(
        connector = "8.0.15"
    )
    const val ivy = "2.5.0-rc1"
    const val guava = "27.0.1-jre"
    const val jcommander = "1.71"
    const val junit5 = "5.4.1"
    const val assertj = "3.12.2"
    const val log4j = "2.11.2"
    const val slf4j = "1.8.0-beta4"
    const val progressBar = "0.7.2"
    const val jline = "3.9.0"
    const val poi = "4.0.1"
    const val detekt = "1.0.0-RC14"
}

data class MysqlVersions(
    val connector: String
)
