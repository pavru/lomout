@file:Suppress("UnstableApiUsage")

import org.gradle.plugins.ide.idea.model.Module

plugins {
    java
    application
    kotlin("jvm") version Versions.kotlin
    idea
    id("jacoco")
}

group = "oooast-tools"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "net.pototskiy.apps.magemediation.MainKt"
}

idea {
    module {
        sourceDirs = setOf(
            file("$projectDir/src/main/kotlin"),
            file("$projectDir/src/main/java"),
            file("$projectDir/config/."),
            file("$rootProject/.testdata/.")
        )
        outputDir = file("build/classes/kotlin/main")
        testOutputDir = file("build/classes/kotlin/test")
        iml {
            whenMerged {
                val iModule = this as Module
                val existingDeps = iModule.dependencies
                iModule.dependencies.clear()
            }
        }
    }
}

kotlin {

}

sourceSets {
    test {
        java {
            //            exclude("**/magemediation/**")
        }
    }
    create("configScripts") {
        java {
            srcDir(file("$projectDir/config"))
            exclude("**/*.kts")
        }
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }

    create("testData") {
        java {
            srcDir(file("$rootProject/.testdata"))
            exclude("**/*.kts")
        }
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val configScriptsImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testDataImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

tasks.named<Test>("test") {
    maxHeapSize = "2G"
    minHeapSize = "1G"
    val travisBuildBir = System.getenv("TEST_DAT_DIR")
    environment("TEST_DATA_DIR", "$projectDir/testdata")
    environment("PRODUCTION_CONFIG", "$projectDir/config/config.conf.kts")
    useJUnitPlatform()
    testLogging {
//        events("passed", "skipped", "failed")
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
    filter {
        includeTestsMatching("*DataLoadingInProductionWayTest*")
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
    kotlinOptions {
        jvmTarget = "1.8"
        noReflect = false
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
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {

    configScriptsImplementation(kotlin("script-util"))
    configScriptsImplementation(project(":mage-mediation-api", "spi"))
    configScriptsImplementation("org.jetbrains.exposed", "exposed", Versions.exposed) {
        exclude("org.jetbrains.kotlin")
        exclude("org.slf4j")
    }
    testDataImplementation(kotlin("script-util"))
    testDataImplementation(project(":mage-mediation-api", "spi"))
    testDataImplementation("org.jetbrains.exposed", "exposed", Versions.exposed) {
        exclude("org.jetbrains.kotlin")
        exclude("org.slf4j")
    }

    implementation(fileTree("lib") {
        include("*.jar")
    })
    implementation(project(":mage-mediation-api"))
//    runtimeOnly(project(":mage-mediation-api", "spi"))
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
    implementation("org.apache.commons", "commons-csv", "1.6")
// MySql
    implementation("mysql", "mysql-connector-java", Versions.mysql.connector)
// Logger
    implementation("org.slf4j", "slf4j-api", "1.8.0-beta2")
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", Versions.log4j)
    implementation("org.apache.logging.log4j", "log4j-core", Versions.log4j)
// Kotlin script
    runtimeOnly(kotlin("script-runtime"))
//    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))
    implementation(kotlin("scripting-jvm-host"))
// Test
// testCompile(group = "junit", name = "junit", version = "4.12")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", Versions.junit5)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", Versions.junit5)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", Versions.junit5)
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.assertj", "assertj-core", Versions.assertj)
}

//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

sonarqube {
    val coverageFiles = fileTree("$projectDir") {
        include("build/jacoco/*.exec")
    }
    val javaBinaries = listOf(
        "$projectDir/build/classes/kotlin/main",
        "$projectDir/build/classes/java/main"
    )
    val testBinaries = listOf(
        "$projectDir/build/classes/kotlin/test",
        "$projectDir/build/classes/java/test"
    )
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.java.source", "1.8")
//        property("sonar.java.binaries", javaBinaries.joinToString(","))
//        property("sonar.java.test.binaries", testBinaries.joinToString(","))
//        property("sonar.jacoco.reportPaths", coverageFiles.joinToString(","))
    }
}
