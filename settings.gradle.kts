rootProject.name = "oooast-tools"
rootProject.buildFileName = "build.gradle.kts"

pluginManagement{
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

include(":import-products")

