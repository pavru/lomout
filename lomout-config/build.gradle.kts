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
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `java-library`
    kotlin("jvm")
    idea
    id("org.jetbrains.dokka") version Versions.dokka
    `maven-publish`
    id("io.gitlab.arturbosch.detekt") version Versions.detekt
    id("jacoco")
//    id ("org.jetbrains.intellij") version "0.4.8"
}

group = rootProject.group
version = rootProject.version

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/kotlin")
        }
    }
}
configurations {
    create("spi")
}

val spiImplementation: Configuration by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

tasks.withType(DokkaTask::class) {
    moduleName = "lomout-api"
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
    samples = listOf()
    jdkVersion = 8

}

tasks.register<Jar>("dokkaJar") {
    group = "documentation"
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
    from(file("$buildDir/javadoc"))
}

tasks.withType(JacocoReport::class.java).all {
    reports {
        xml.isEnabled = true
    }
}

tasks.jar {
    group = "build"
    dependsOn(configurations.runtimeClasspath)
    from(project.the<SourceSetContainer>()["main"].output)
    from({
        configurations.runtimeClasspath.get()
            .filter {
                it.name.endsWith("jar") &&
                        (it.name.contains("ivy") ||
                                it.name.contains("kmongo-property") ||
                                it.name.contains("log4j-api") ||
                                it.name.contains("bson") ||
                                it.name.contains("kotlin-script-util")
                                )
            }
            .map { zipTree(it) }
    })
    from("./src/main") {
        include("META-INF/**")
    }
    from(rootProject.rootDir) {
        include("LICENSE", "NOTICE")
        into("/")
    }
}

artifacts {
    add("spi", tasks["jar"])
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Test>("test") {
    if (System.getenv("TRAVIS_BUILD_DIR") == null) {
        maxHeapSize = "2G"
        minHeapSize = "1G"
        systemProperties(
            mapOf(
                "junit.jupiter.execution.parallel.enabled" to "true",
                "junit.jupiter.execution.parallel.config.strategy" to "dynamic"
            )
        )
        testLogging {
            events(
                /*"passed",*/
                "skipped",
                "failed"/*,
                "standardOut",
                "standardError"*/
            )
        }
    } else {
//        setForkEvery(14)
        maxHeapSize = "700M"
        minHeapSize = "300M"
        systemProperties(
            mapOf(
                "junit.jupiter.execution.parallel.enabled" to "flase",
                "junit.jupiter.execution.parallel.config.strategy" to "fixed",
                "junit.jupiter.execution.parallel.config.fixed.parallelism" to 1
            )
        )
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
    environment("TEST_DATA_DIR", "${rootProject.projectDir}/testdata")
    environment("PRODUCTION_CONFIG", "${rootProject.projectDir}/config/config.conf.kts")
    @Suppress("UnstableApiUsage")
    useJUnitPlatform {
    }
}

repositories {
    jcenter()
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))

    compileOnly(files("$projectDir/lib/util.jar"))
    implementation("org.apache.ivy", "ivy", Versions.ivy)
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
    implementation("org.apache.logging.log4j", "log4j-api", Versions.log4j)
    // Apache commons io
    implementation("commons-io", "commons-io", "2.6")
    // Kotlin script
    runtimeOnly(kotlin("script-runtime"))
//    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))
//    implementation(kotlin("scripting-jvm-host"))
    implementation(kotlin("scripting-jvm-host"))
    // Cache
    implementation("org.cache2k", "cache2k-api", Versions.cache2k)
    runtimeOnly("org.cache2k", "cache2k-core", Versions.cache2k)
    // Test
    testImplementation("org.junit.jupiter", "junit-jupiter-api", Versions.junit5)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", Versions.junit5)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", Versions.junit5)
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.assertj", "assertj-core", Versions.assertj)
    // Addon
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}")
}

tasks.register<Jar>("sourcesJar") {
    archiveBaseName.set("lomout-api")
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["dokkaJar"])
            artifact(tasks["sourcesJar"])
        }
    }
    repositories {
        mavenLocal()
    }
}

detekt {
    config = files("${rootProject.projectDir}/detekt-config.yml")
    failFast = false
}
