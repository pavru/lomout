import name.remal.gradle_plugins.dsl.extensions.java

plugins {
    id("java")
    kotlin("jvm") version "1.3.11"
    id("idea")
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

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
    kotlinOptions {
        jvmTarget = "1.6"
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
    compile(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")
    compile(group = "com.sun.xml.bind", name = "jaxb-core", version = "2.3.0.1")
    compile(group = "com.sun.xml.bind", name = "jaxb-impl", version = "2.3.1")
    compile(group = "com.sun.xml.bind", name = "jaxb-jxc", version = "2.3.1")
    // Test
    testCompile(group = "junit", name = "junit", version = "4.12")
}

//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}