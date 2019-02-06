plugins {
    `java-library`
    kotlin("jvm") version Versions.kotlin
    idea
}

group = "oooast-tools"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

idea {
    module {
        outputDir = file("build/classes/kotlin/main")
        testOutputDir = file("build/classes/kotlin/test")
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(project(":mage-mediation-api", "spi"))
    // Database
    implementation("org.jetbrains.exposed",  "exposed",  Versions.exposed) {
        exclude("org.jetbrains.kotlin")
        exclude("org.slf4j")
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    kotlinOptions {
        jvmTarget = "1.8"
        noReflect = false
    }
}
