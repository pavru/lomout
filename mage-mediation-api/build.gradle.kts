plugins {
    `java-library`
    kotlin("jvm") version "1.3.11"
    idea
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
    runtimeOnly(project(":mage-mediation-config-dsl"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    // Database
    implementation(group = "org.jetbrains.exposed", name = "exposed", version = "0.11.2") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.slf4j")
    }
    // Excel
    implementation(group = "org.apache.poi", name = "poi", version = "4.0.1")
    implementation(group = "org.apache.poi", name = "poi-ooxml", version = "4.0.1")
    // CSV
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.6")
    // MySql
    implementation(group = "mysql", name = "mysql-connector-java", version = "8.0.13")
    // Logger
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.8.0-beta2")
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", "2.11.1")
    // Apache commons io
    implementation("commons-io", "commons-io", "2.6")
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