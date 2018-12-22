rootProject.name = "oooast-tools"
rootProject.buildFileName = "build.gradle.kts"

pluginManagement {
    repositories {
        @Suppress("UnstableApiUsage")
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

include(":mage-mediation")

