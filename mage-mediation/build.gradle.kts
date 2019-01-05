import name.remal.gradle_plugins.dsl.extensions.java
import name.remal.gradle_plugins.dsl.extensions.testImplementation
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    id("java")
    kotlin("jvm") version "1.3.11"
    id("idea")
    id("jacoco")
}

group = "oooast-tools"
version = "1.0-SNAPSHOT"

idea {
    module {
        outputDir = file("build/classes/kotlin/main")
        testOutputDir = file("build/classes/kotlin/test")
    }
}

kotlin {

}

tasks.test {
    @Suppress("UnstableApiUsage")
    useJUnitPlatform()
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

repositories {
    jcenter()
}



dependencies {
    compile(project(":mage-mediation-api"))
    compile(project(":mage-mediation-category"))
    compile(kotlin("stdlib-jdk8"))
    compile(group = "com.beust", name = "jcommander", version = "1.71")
    // Database
    compile(group = "org.jetbrains.exposed", name = "exposed", version = "0.11.2")
    // Excel
    compile(group = "org.apache.poi", name = "poi", version = "4.0.1")
    compile(group = "org.apache.poi", name = "poi-ooxml", version = "4.0.1")
    // CSV
    compile(group = "org.apache.commons", name = "commons-csv", version = "1.6")
    // MySql
    compile(group = "mysql", name = "mysql-connector-java", version = "8.0.13")
    // Logger
    compile(group = "org.slf4j", name = "slf4j-api", version = "1.8.0-beta2")
    compile(group = "org.slf4j", name = "slf4j-log4j12", version = "1.8.0-beta2")
    // JAXB
//    compile(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")
//    compile(group = "com.sun.xml.bind", name = "jaxb-core", version = "2.3.0.1")
//    compile(group = "com.sun.xml.bind", name = "jaxb-impl", version = "2.3.1")
//    compile(group = "com.sun.xml.bind", name = "jaxb-jxc", version = "2.3.1")
    // Test
    // testCompile(group = "junit", name = "junit", version = "4.12")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.0-rc.1").apply {
        exclude("org.jetbarins.kotlin")
    }
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.0-rc.1").apply {
    }
    testImplementation(group = "org.amshove.kluent", name = "kluent", version = "1.45")
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
