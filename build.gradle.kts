@file:Suppress("UnstableApiUsage")

plugins {
    id("com.github.ben-manes.versions") version "0.21.0"
    id("org.sonarqube") version "2.7"
    jacoco
}

buildscript {
}

group = "lomout"
version = "1.1.0"

subprojects {
    apply {
        plugin("java")
        plugin("jacoco")
    }
}

repositories {
    jcenter()
    mavenCentral()
}
tasks["sonarqube"].group = "verification"

jacoco {
    toolVersion = "0.8.3"
}

task<JacocoReport>("codeCoverageReport") {
    group = "verification"

    executionData.setFrom(fileTree(project.rootDir.absolutePath).apply {
        include("**/build/jacoco/*.exec")
    })

    subprojects {
        this@task.sourceSets(the<SourceSetContainer>()["main"])
    }

    reports {
        xml.isEnabled = true
        xml.destination = file("$buildDir/reports/jacoco/jacocoCoverageReport.xml")
        html.isEnabled = true
        html.destination = file("$buildDir/reports/jacoco/report.xml")
        csv.isEnabled = false
    }
}
