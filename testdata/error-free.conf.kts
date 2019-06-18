@file:DependsOn("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib", "1.3.20")

config {
    @Suppress("RedundantExplicitType") val a: Int = 100
    database {
        name("test_lomunt")
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
        productionLine {
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
