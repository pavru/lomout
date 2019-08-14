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

@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.detekt

plugins {
    java
    application
    kotlin("jvm")
    idea
    id("jacoco")
    id("io.gitlab.arturbosch.detekt") version Versions.detekt
}

group = rootProject.group
version = rootProject.version

application {
    mainClassName = "net.pototskiy.apps.lomout.MainKt"
}

idea {
    module {
        sourceDirs.addAll(
            setOf(
                file("${rootProject.projectDir}/config/."),
                file("${rootProject.projectDir}/testdata/.")
            )
        )
    }
}

sourceSets {
    create("config") {
        java {
            srcDir(file("${rootProject.projectDir}/config"))
            exclude("**/*.kts")
        }
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }

    create("testdata") {
        java {
            srcDir(file("${rootProject.projectDir}/testdata"))
            exclude("**/*.kts")
        }
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
    main {
        java {
            srcDir("${project.buildDir}/generated/kotlin")
        }
    }
}

val configImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testdataImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

tasks.named<Test>("test") {
    maxHeapSize = "2G"
    minHeapSize = "1G"
    systemProperties(
        mapOf(
            "junit.jupiter.execution.parallel.enabled" to "true",
            "junit.jupiter.execution.parallel.config.strategy" to "dynamic"
        )
    )
    environment("TEST_DATA_DIR", "${rootProject.projectDir}/testdata")
    environment("PRODUCTION_CONFIG", "${rootProject.projectDir}/config/config.conf.kts")
    useJUnitPlatform()
    if (System.getenv("TRAVIS_BUILD_DIR") == null) {
        testLogging {
            events(
                "passed",
                "skipped",
                "failed"
            )
        }
    } else {
        testLogging {
            events(
                /*"passed",*/
                "skipped",
                "failed"/*,
                "standardOut",
                "standardError"*/
            )
        }
    }
}

tasks.withType(JacocoReport::class.java).all {
    reports {
        xml.isEnabled = true
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.jar {
    archiveBaseName.set("lomout")
    manifest {
        attributes("Main-Class" to application.mainClassName)
    }
    dependsOn(configurations.runtimeClasspath)
    from(rootProject.rootDir) {
        include("LICENSE", "NOTICE")
    }
    //    from({
//        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//    })
}

repositories {
    jcenter()
}

dependencies {

    configImplementation(kotlin("script-util"))
    configImplementation(project(":lomout-config"))

    testdataImplementation(kotlin("script-util"))
    testdataImplementation(project(":lomout-config"))

    implementation(fileTree("lib") {
        include("*.jar")
    })
    implementation(project(":lomout-config"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.beust", "jcommander", Versions.jcommander)
// Excel
    implementation("org.apache.poi", "poi", Versions.poi)
    implementation("org.apache.poi", "poi-ooxml", Versions.poi)
    // CSV
    implementation("org.apache.commons", "commons-csv", Versions.commonCsv)
    // MongoDB
    implementation("org.litote.kmongo", "kmongo-native", Versions.kmongo)
    // Logger
    implementation("org.slf4j", "slf4j-api", Versions.slf4j)
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", Versions.log4j)
    implementation("org.apache.logging.log4j", "log4j-core", Versions.log4j)
    implementation("org.fusesource.jansi", "jansi", "1.18")
    // Kotlin script
    runtimeOnly(kotlin("script-runtime"))
    //    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))
    implementation(kotlin("scripting-jvm-host"))
    // Cachw 2k
    implementation("org.cache2k", "cache2k-api", Versions.cache2k)
    runtimeOnly("org.cache2k", "cache2k-core", Versions.cache2k)
    // Test
    // testCompile(group = "junit", name = "junit", version = "4.12")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", Versions.junit5)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", Versions.junit5)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", Versions.junit5)
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.assertj", "assertj-core", Versions.assertj)
    // Addon
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}")
    testImplementation("org.jetbrains.kotlinx", "kotlinx-coroutines-debug", "1.3.0-RC")
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

detekt {
    config = files("${rootProject.projectDir}/detekt-config.yml")
    failFast = false
}
