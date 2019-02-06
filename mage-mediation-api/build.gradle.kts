import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `java-library`
    kotlin("jvm") version Versions.kotlin
    idea
    id("org.jetbrains.dokka") version Versions.dokka
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

val spiImplementation: Configuration by configurations.creating() {
    extendsFrom(configurations.implementation.get())
}

tasks.withType(DokkaTask::class) {
    moduleName = "mage-mediation"
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

tasks.register<Jar>("dokkaJar") {
    group = "documentation"
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
    from(file("$buildDir/javadoc"))
}

tasks.register<Jar>("spiJar") {
    group = "build"
    archiveBaseName.set("mage-mediation-api")
    dependsOn(configurations.runtimeClasspath)
    from(project.the<SourceSetContainer>()["main"].output)
//    include("net/pototskiy/apps/magemediation/**")
//    include("META_INF/**")
}

artifacts {
    add("spi", tasks["spiJar"])
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
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
    // Database
    implementation("org.jetbrains.exposed", "exposed", Versions.exposed) {
        exclude("org.jetbrains.kotlin")
        exclude("org.slf4j")
    }
    // Excel
    implementation("org.apache.poi", "poi", "4.0.1")
    implementation("org.apache.poi", "poi-ooxml", "4.0.1")
    // CSV
    implementation("org.apache.commons", "commons-csv", "1.6")
    // MySql
    implementation("mysql", "mysql-connector-java", Versions.mysql.connector)
    // Logger
    implementation("org.slf4j", "slf4j-api", "1.8.0-beta2")
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", "2.11.1")
    // Apache commons io
    implementation("commons-io", "commons-io", "2.6")
    // Kotlin script
    runtimeOnly(kotlin("script-runtime"))
//    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))
//    implementation(kotlin("scripting-jvm-host"))
    implementation(kotlin("scripting-jvm-host"))
    // Test
    testImplementation("junit", "junit", "4.12")
}

//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
