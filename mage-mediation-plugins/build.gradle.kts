
plugins {
    `java-library`
    kotlin("jvm") version "1.3.11"
    idea
}

group  = "oooast-tools"
version  = "1.0-SNAPSHOT"

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
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(project(":mage-mediation-api"))
    // Database
    implementation(group = "org.jetbrains.exposed", name = "exposed", version = "0.11.2") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.slf4j")
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    kotlinOptions{
        jvmTarget = "1.8"
        noReflect = false
    }
}
