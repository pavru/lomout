rootProject.name = "oooast-tools"
rootProject.buildFileName = "build.gradle.kts"

pluginManagement {
    repositories {
        @Suppress("UnstableApiUsage")
        gradlePluginPortal()
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/kotlin/kotlin-dev/")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.junit.platform.gradle.plugin" ->
                    useModule("org.junit.platform:junit-platform-gradle-plugin:${requested.version}")
                "org.jetbrains.dokka" ->
                    useModule("org.jetbrains.dokka:dokka-gradle-plugin:${requested.version}")
            }
        }
    }
}

include(
    ":mage-mediation-api",
    ":mage-mediation"
)

