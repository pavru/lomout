rootProject.name = "oooast-tools"
rootProject.buildFileName = "build.gradle.kts"

pluginManagement {
    repositories {
        @Suppress("UnstableApiUsage")
        gradlePluginPortal()
        mavenCentral()
        jcenter()
        maven(url="http://dl.bintray.com/jetbrains/spek")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.junit.platform.gradle.plugin" ->
                    useModule("org.junit.platform:junit-platform-gradle-plugin:${requested.version}")
            }
        }
    }
}

include(":mage-mediation", ":mage-mediation-api")

