/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
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
        createDecenciesList(dependencies)
        val dirGenerated = File(
            "${project.buildDir}/generated/kotlin"
        )
        buildFileBuildInfo(dependencies).writeTo(dirGenerated)
    }

    @Suppress("NestedBlockDepth")
    private fun createDecenciesList(dependencies: MutableList<String>) {
        val excludeClassName = ClassName("", buildExcludeRuleClass().name!!)
        val dependencyClassName = ClassName("", buildDependencyClass().name!!)
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
                                @Suppress("USELESS_ELVIS")
                                excludeRules.add(Pair(it.group ?: "*", it.module ?: "*"))
                            }
                        }
                        val rules = excludeRules.joinToString(",") {
                            """${excludeClassName.simpleName}("${it.first}","${it.second}")"""
                        }
                        dependencies.add(
                            """${dependencyClassName.simpleName}(
                                |"${configuration.name}", 
                                |"$group", 
                                |"$name", 
                                |"$version", 
                                |listOf<${excludeClassName.simpleName}>($rules))""".trimMargin()
                        )
                    }
            }
        }
    }

    private fun buildFileBuildInfo(dependencies: List<String>): FileSpec {
        val dependencyClassSpec = buildDependencyClass()
        val listOfDeps = ClassName("kotlin.collections", "MutableList").parameterizedBy(
            ClassName("", dependencyClassSpec.name!!)
        )
        return FileSpec.builder(packageName, objectName)
            .addType(
                TypeSpec.objectBuilder(objectName)
                    .addKdoc("Build info object")
                    .addType(buildExcludeRuleClass())
                    .addType(dependencyClassSpec)
                    .addProperty(
                        PropertySpec.builder("lomoutVersion", String::class)
                            .addKdoc("LoMout application version")
                            .addAnnotation(
                                AnnotationSpec.builder(Suppress::class)
                                    .addMember("%S", "unused")
                                    .build()
                            )
                            .initializer("%S", project.version.toString())
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("kotlinVersion", String::class)
                            .addKdoc("Used Kotlin version")
                            .addAnnotation(
                                AnnotationSpec.builder(Suppress::class)
                                    .addMember("%S", "unused")
                                    .build()
                            )
                            .initializer("%S", Versions.kotlin)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("dependencies", listOfDeps)
                            .addKdoc("List of dependency to use in script")
                            .addAnnotation(
                                AnnotationSpec.builder(Suppress::class)
                                    .addMember("%S", "unused")
                                    .build()
                            )
                            .initializer("mutableListOf(${dependencies.joinToString(",\n")})")
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("moduleName", String::class)
                            .addKdoc("Gradle module name")
                            .addAnnotation(
                                AnnotationSpec.builder(Suppress::class)
                                    .addMember("%S", "unused")
                                    .build()
                            )
                            .initializer("%S", project.name)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun buildDependencyClass(): TypeSpec {
        val excludeRuleClassName = ClassName("", buildExcludeRuleClass().name!!)
        val listOfRule = List::class.asClassName().parameterizedBy(excludeRuleClassName)

        return TypeSpec.classBuilder("Dependency")
            .addKdoc(
                """
                Dependency definition
                
                @property configuration The source configuration of dependency
                @property group The dependency group(org) name
                @property name The dependency module(artifact) name
                @property version The artifact version
                @property excludeRules The dependencies list to exclude
            """.trimIndent().trimMargin()
            )
            .addModifiers(KModifier.DATA)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("configuration", String::class)
                    .addParameter("group", String::class)
                    .addParameter("name", String::class)
                    .addParameter("version", String::class)
                    .addParameter(
                        ParameterSpec.builder("excludeRules", listOfRule)
                            .defaultValue("emptyList()")
                            .build()
                    )
                    .build()
            )
            .addProperty(
                PropertySpec.builder("configuration", String::class)
                    .initializer("configuration")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("group", String::class)
                    .initializer("group")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("name", String::class)
                    .initializer("name")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("version", String::class)
                    .initializer("version")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("excludeRules", listOfRule)
                    .initializer("excludeRules")
                    .build()
            )
            .build()
    }

    private fun buildExcludeRuleClass(): TypeSpec {
        return TypeSpec.classBuilder("ExcludeRule")
            .addKdoc(
                """
                Dependency exclude rule
                
                @property group The dependency group(org) name
                @property name The dependency module(artifact) name
                """.trimIndent().trimMargin()
            )
            .addModifiers(KModifier.DATA)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder("group", String::class)
                            .defaultValue("%S", "*")
                            .build()
                    )
                    .addParameter(
                        ParameterSpec.builder("name", String::class)
                            .defaultValue("%S", "*")
                            .build()
                    )
                    .build()
            )
            .addProperty(
                PropertySpec.builder("group", String::class)
                    .initializer("group")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("name", String::class)
                    .initializer("name")
                    .build()
            )
            .build()
    }
}
