class TestType: Document() {
    @Key
    var attr: String = ""
    companion object: DocumentMetadata(TestType::class)
}
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
        loadEntity(TestType::class) {
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
