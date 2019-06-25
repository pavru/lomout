config {
    database {
        name("test")
        server {
            host("localhost")
            user("root")
        }
    }
    loader {
        files {
            file("file-1") { path("test path") }
        }
        entities {
            entity("test", false) {
                attribute<STRING>("attr") { key() }
            }
        }
        loadEntity("test") {
            fromSources {
                source { file("file-2"); sheet("test"); stopOnEmptyRow() }
            }
            sourceFields {
                main("test") {
                    field("attr")
                }
            }
        }
    }
}
