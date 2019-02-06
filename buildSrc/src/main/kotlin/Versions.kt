object Versions {
    const val kotlin = "1.3.20"
    const val dokka = "0.9.17"
    const val exposed = "0.12.1"
    val mysql = MysqlVersions(
        connector = "8.0.15"
    )
    const val ivy = "2.4.0"
    const val guava = "27.0.1-jre"
    const val jcommander = "1.71"
    const val junit5 = "5.3.2"
    const val assertj = "3.11.1"
}

data class MysqlVersions(
    val connector: String
)
