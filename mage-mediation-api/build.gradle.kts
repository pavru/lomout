@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.detekt
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm") version Versions.kotlin
    idea
    id("org.jetbrains.dokka") version Versions.dokka
    `maven-publish`
    id("io.gitlab.arturbosch.detekt") version Versions.detekt
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

configurations {
    create("spi")
}

val spiImplementation: Configuration by configurations.creating {
    extendsFrom(configurations.implementation.get())
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
    }
}
tasks.withType(DokkaTask::class) {
    moduleName = "mage-mediation-api"
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
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
    archiveBaseName.set("mage-mediation-api")
    dependsOn(configurations.runtimeClasspath)
    from(project.the<SourceSetContainer>()["main"].output)
//    include("net/pototskiy/apps/magemediation/**")
//    include("META_INF/**")
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") && it.name.contains("ivy")}
            .map { zipTree(it) }
    })}

artifacts {
    add("spi", tasks["jar"])
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        noReflect = false
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
            "-Xuse-experimental=kotlin.Experimental"
        )
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Test>("test") {
    maxHeapSize = "2G"
    minHeapSize = "1G"
    val travisBuildBir = System.getenv("TEST_DAT_DIR")
    environment("TEST_DATA_DIR", "$projectDir/testdata")
    environment("PRODUCTION_CONFIG", "$projectDir/config/config.conf.kts")
    @Suppress("UnstableApiUsage")
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
//        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

repositories {
    jcenter()
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))

    implementation("org.apache.ivy", "ivy", Versions.ivy)
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
    // Apache commons io
    implementation("commons-io", "commons-io", "2.6")
    // Kotlin script
    runtimeOnly(kotlin("script-runtime"))
//    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))
//    implementation(kotlin("scripting-jvm-host"))
    implementation(kotlin("scripting-jvm-host"))
    // Test
    testImplementation("org.junit.jupiter", "junit-jupiter-api", Versions.junit5)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", Versions.junit5)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", Versions.junit5)
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.assertj", "assertj-core", Versions.assertj)
    // Addon
    detektPlugins ("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}")
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
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
