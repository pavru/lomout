plugins {
    id("com.github.ben-manes.versions") version "0.21.0"
    id("org.sonarqube") version "2.7"
}

buildscript {
}

group = "oooast-tools"
version = "1.0-SNAPSHOT"

sonarqube {
    properties {
//        property("sonar.host.url", "https://sonarcloud.io")
//        property("sonar.login", "e06a94fe1c8c6c7f15ab33907bd5906e76050ef3")
//        property("sonar.organizationKey", "pavru-github")
        property("sonar.organization","pavru-github")
        property("sonar.projectKey", "my:pavru_oooast-tools")
        property("sonar.projectName", "oooast-tools")
        property("sonar.projectVersion", "1.0-SNAPSHOT")
    }
}

tasks["sonarqube"].group = "verification"
