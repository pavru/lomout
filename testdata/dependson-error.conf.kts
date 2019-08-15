/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

@file:DependsOn("test", "group", "absent-file.plugin.conf.kts", "1.0")

import net.pototskiy.apps.lomout.api.document.emptyDocumentData

config {
    database {
        name("test_lomout")
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
                assembler { _, _ -> emptyDocumentData() }
            }
        }

    }
}
