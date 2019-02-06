plugins {
    id("com.github.ben-manes.versions") version "0.20.0"
    id("org.sonarqube") version "2.7"
}

buildscript {
}

group = "oooast-tools"
version = "1.0-SNAPSHOT"

sonarqube {
    properties {
        property("sonar.host.url", "https://sonar.local")
        property("sonar.login", "alex")
        property("sonar.password", "xxsystem")
        property("sonar.projectKey", "oooast-tools")
        property("sonar.projectVersion", "1.0")
    }
}

tasks["sonarqube"].group = "verification"
