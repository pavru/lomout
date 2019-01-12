plugins {
    java
    kotlin("jvm") version "1.3.11"
    idea
    id("jacoco")
}

group = "oooast-tools"
version = "1.0-SNAPSHOT"

idea {
    module {
        sourceDirs = setOf(
            file("$projectDir/src/main/kotlin"),
            file("$projectDir/src/main/java"),
            file("$projectDir/config/.")
        )
        outputDir = file("build/classes/kotlin/main")
        testOutputDir = file("build/classes/kotlin/test")
    }
}

kotlin {

}

sourceSets {
    main {
        //        java.srcDir(file("$projectDir/config/."))
    }
    create("config") {
        java.srcDir(file("$projectDir/config"))
//        compileClasspath += configurations.getByName("configImplementation")
//        runtimeClasspath += sourceSets.main.get().output
    }
}

val configImplementation = configurations.getByName(sourceSets.getByName("config").implementationConfigurationName)

tasks.test {
    @Suppress("UnstableApiUsage")
    useJUnitPlatform() {
        includeEngines("spek2")
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
        attributes("Main-Class" to "net.pototskiy.apps.magemediation.MainKt")
    }
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

repositories {
    jcenter()
}

dependencies {
    configImplementation(project(":mage-mediation-config-dsl"))
    configImplementation(project(":mage-mediation-plugins"))
    configImplementation(kotlin("script-runtime"))
//    configImplementation(kotlin("compiler-embeddable"))
//    configImplementation(kotlin("script-util"))

    runtimeOnly(project(":mage-mediation-plugins"))
    implementation(project(":mage-mediation-api"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(group = "com.beust", name = "jcommander", version = "1.71")
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
    implementation("org.slf4j", "slf4j-api", "1.8.0-beta2")
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", "2.11.1")
    implementation("org.apache.logging.log4j","log4j-core","2.11.1")
    // Kotlin script
    implementation(kotlin("script-runtime"))
    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))
    // Test
    // testCompile(group = "junit", name = "junit", version = "4.12")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.0-rc.1") {
        exclude("org.jetbarins.kotlin")
    }
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.0-rc.1") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(group = "org.amshove.kluent", name = "kluent", version = "1.45")
    testRuntimeOnly(project(":mage-mediation-plugins"))
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
