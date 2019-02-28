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
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", "5c3469fb9d814033e8aaa5a0983d59e5c51f94bb")
        property("sonar.organizationKey", "pavru-github")
        property("sonar.organization","pavru-github")
        property("sonar.projectKey", "pavru_oooast-tools")
        property("sonar.projectName", "oooast-tools")
        property("sonar.projectVersion", "1.0-SNAPSHOT")
    }
}

tasks["sonarqube"].group = "verification"
