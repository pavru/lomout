@file:Suppress("UnstableApiUsage")

plugins {
    id("com.github.ben-manes.versions") version "0.21.0"
    id("org.sonarqube") version "2.7"
    jacoco
}

buildscript {
}

group = "oooast-tools"
version = "1.0-SNAPSHOT"

sonarqube {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", "5c3469fb9d814033e8aaa5a0983d59e5c51f94bb")
        property("sonar.organization", "pavru-github")
        property("sonar.projectKey", "pavru_oooast-tools")
        property("sonar.projectName", "oooast-tools")
        property("sonar.projectVersion", "1.0-SNAPSHOT")
    }
}
allprojects {
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

task<JacocoReport>("codeCoverageReport") {
    group = "verification"

    executionData.setFrom(fileTree(project.rootDir.absolutePath).apply {
        include("**/build/jacoco/*.exec")
    })

    subprojects {
        val v = the<SourceSetContainer>()["main"]
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

