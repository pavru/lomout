import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateBuildClassTask : DefaultTask() {
    @Input
    var addDependenciesOfConfigurations = listOf("implementation")
    @Input
    var packageName: String = ""
    @Input
    var objectName: String = "BuildInfo"

    @TaskAction
    fun generate() {
        val dependencies = mutableListOf<String>()
        project.configurations.forEach { configuration ->
            if (configuration.name in addDependenciesOfConfigurations) {
                configuration.dependencies
                    .filterNot { it.group == null }
                    .forEach { dep ->
                        val group = dep.group
                        val name = dep.name
                        val version = dep.version
                        val excludeRules = mutableListOf<Pair<String, String>>()
                        if (dep is DefaultExternalModuleDependency) {
                            dep.excludeRules.forEach {
                                excludeRules.add(Pair(it.group ?: "*", it.module ?: "*"))
                            }
                        }
                        val rules = excludeRules.joinToString(",") {
                            """ExcludeRule("${it.first}","${it.second}")"""
                        }
                        dependencies.add(
                            """Dependency("${configuration.name}", "$group", "$name", "$version", listOf<ExcludeRule>($rules))"""
                        )
                    }
            }
        }
        val buildClassFile = File(
            "${project.buildDir}/generated/kotlin/${packageName.replace(".", "/")}/$objectName.kt"
        )
        buildClassFile.parentFile.mkdirs()
        buildClassFile.writeText(
            """
            package $packageName
            object $objectName {
                val lomoutVersion = "${project.version}"
                val kotlinVersion = "${Versions.kotlin}"
                val dependencies = listOf<Dependency>(${dependencies.joinToString(",")})

                data class Dependency(
                    val configuration: String,
                    val group: String,
                    val name: String,
                    val version: String,
                    val excludeRules: List<ExcludeRule> = emptyList()
                )
                data class ExcludeRule(
                    val group: String = "*",
                    val name: String = "*"
                )
            }

        """.trimIndent()
        )
    }
}
