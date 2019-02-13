@file:Import("transformer/onecGroupToLong.plugin.kts")

config {
    database {
        name("test")
        server {
            host("127.0.0.1")
        }
    }
    loader {
        files {

        }
        onecGroup("test") {
            sources { source { file("test").sheet(Regex(".*")).ignoreEmptyRows() } }
            main("test") {
                field("code") { withTransform(onecGroupToLongPlugin) } to attribute { type { int() } }
            }
        }
    }
}
