@file:DependsOn("test", "group", "absent-file.plugin.conf.kts", "1.0")

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
                attribute<StringType>("attr") { key() }
            }
        }
        loadEntity("test") {
            fromSources {
                source { file("file-1"); sheet("test"); stopOnEmptyRow() }
            }
            sourceFields {
                main("test") {
                    field("attr") { column(0) }
                }
            }
        }
    }
    mediator {
        unionProductionLine {
            input {
                entity("test")
            }
            output("import-test") {
                inheritFrom("test")
            }
            pipeline {
                assembler { _, _ -> emptyMap() }
            }
        }

    }
}
