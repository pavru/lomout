import name.remal.gradle_plugins.dsl.extensions.java
import name.remal.gradle_plugins.dsl.extensions.runtime
import name.remal.gradle_plugins.dsl.extensions.testImplementation

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
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.beust", name = "jcommander", version = "1.71")
    // Database
    implementation(group = "org.jetbrains.exposed", name = "exposed", version = "0.11.2")
    // Excel
    implementation(group = "org.apache.poi", name = "poi", version = "4.0.1")
    implementation(group = "org.apache.poi", name = "poi-ooxml", version = "4.0.1")
    // CSV
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.6")
    // MySql
    implementation(group = "mysql", name = "mysql-connector-java", version = "8.0.13")
    // Logger
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.8.0-beta2")
    implementation(group = "org.slf4j", name = "slf4j-log4j12", version = "1.8.0-beta2")
    // Apache commons io
    implementation("commons-io", "commons-io", "2.6")
    // JAXB
    implementation("javax.xml.bind", "jaxb-api", "2.3.1")
    implementation("com.sun.xml.bind", "jaxb-core", "2.3.0.1")
    //implementation(group = "com.sun.xml.bind", name = "jaxb-impl", version = "2.3.1")
    //implementation(group = "com.sun.xml.bind", name = "jaxb-jxc", version = "2.3.1")
    // XML
    implementation(group = "xerces", name = "xercesImpl", version = "2.12.0")
    // Kotlin script
    implementation(kotlin("script-runtime"))
    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))
    // Test
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}