@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.detekt
import org.gradle.plugins.ide.idea.model.Module
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
        outputDir = file("build/classes/kotlin/main")
        testOutputDir = file("build/classes/kotlin/test")
        iml {
            whenMerged {
                val iModule = this as Module
                iModule.dependencies.clear()
            }
        }
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

tasks.register<GenerateBuildClassTask>("generateBuildClass") {
    packageName = "net.pototskiy.apps.lomout"
    objectName = "BuildInfo"
    addDependenciesOfConfigurations = listOf()
    this.group = "build"
}

tasks.named<Test>("test") {
    maxHeapSize = "2G"
    minHeapSize = "1G"
    systemProperties(
        mapOf(
            "junit.jupiter.execution.parallel.enabled" to "true",
            "junit.jupiter.execution.parallel.config.strategy" to "dynamic"
//    "junit.jupiter.execution.parallel.mode.default" to "concurrent"
        )
    )
    environment("TEST_DATA_DIR", "${rootProject.projectDir}/testdata")
    environment("PRODUCTION_CONFIG", "${rootProject.projectDir}/config/config.conf.kts")
    useJUnitPlatform()
    testLogging {
        events(
            /*"passed",*/
            "skipped",
            "failed"
        )
//        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

tasks.withType<KotlinCompile> {
    dependsOn += tasks["generateBuildClass"]
    kotlinOptions {
        jvmTarget = "1.8"
        noReflect = false
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
            "-Xuse-experimental=kotlin.Experimental",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi"
        )
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
    manifest {
        attributes("Main-Class" to application.mainClassName)
    }
    dependsOn(configurations.runtimeClasspath)
//    from({
//        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//    })
}

repositories {
    jcenter()
}

dependencies {

    configImplementation(kotlin("script-util"))
    configImplementation("lomout", "lomout", "1.0-SNAPSHOT")
    configImplementation("org.jetbrains.exposed", "exposed", Versions.exposed) {
        exclude("org.jetbrains.kotlin")
        exclude("org.slf4j")
    }
    testdataImplementation(kotlin("script-util"))
    testdataImplementation("lomout", "lomout", "1.0-SNAPSHOT")
    testdataImplementation("org.jetbrains.exposed", "exposed", Versions.exposed) {
        exclude("org.jetbrains.kotlin")
        exclude("org.slf4j")
    }

    implementation(fileTree("lib") {
        include("*.jar")
    })
    implementation(project(":lomout-api"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.beust", "jcommander", Versions.jcommander)
// Database
    implementation("org.jetbrains.exposed", "exposed", Versions.exposed) {
        exclude("org.jetbrains.kotlin")
        exclude("org.slf4j")
    }
// Excel
    implementation("org.apache.poi", "poi", Versions.poi)
    implementation("org.apache.poi", "poi-ooxml", Versions.poi)
// CSV
    implementation("org.apache.commons", "commons-csv", Versions.commonCsv)
// MySql
    implementation("mysql", "mysql-connector-java", Versions.mysql.connector)
// Logger
    implementation("org.slf4j", "slf4j-api", Versions.slf4j)
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", Versions.log4j)
    implementation("org.apache.logging.log4j", "log4j-core", Versions.log4j)
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
