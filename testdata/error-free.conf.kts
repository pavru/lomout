//@file:DependsOn("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib", "1.3.40")

open class Test : Document() {
    @Key
    var attr: String = ""

    companion object : DocumentMetadata(Test::class)
}

class ImportTest : Test() {
    companion object : DocumentMetadata(ImportTest::class)
}

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
        loadEntity(Test::class) {
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
                entity(Test::class)
            }
            output(ImportTest::class)
            pipeline {
                assembler { _, _ -> emptyMap() }
            }
        }

    }
}
