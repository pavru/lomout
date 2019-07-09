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

@file:Suppress("UnstableApiUsage")

import org.gradle.plugins.ide.idea.model.Module
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version "0.21.0"
    id("org.sonarqube") version "2.7.1"
    jacoco
    idea
    kotlin("jvm") version Versions.kotlin
    id("com.gradle.build-scan") version "2.3"
}

buildscript {
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

group = "lomout"
version = "1.3.0"

idea {
    project {
        jdkName = "1.8"
        setLanguageLevel(JavaVersion.VERSION_1_8)
    }
}
subprojects {
    apply {
        plugin("java")
        plugin("jacoco")
        plugin("idea")
    }
    tasks.register<GenerateBuildClassTask>("generateBuildClass") {
        packageName = when (this.project.name) {
            "lomout-api" -> "net.pototskiy.apps.lomout.api"
            else -> "net.pototskiy.apps.lomout"
        }
        objectName = "BuildInfo"
        addDependenciesOfConfigurations = listOf()
        this.group = "build"
    }
    tasks.withType<KotlinCompile> {
        dependsOn += tasks["generateBuildClass"]
        kotlinOptions {
            jvmTarget = "1.8"
            noReflect = false
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
                "-Xuse-experimental=kotlin.Experimental",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi",
                "-Xuse-experimental=kotlin.ExperimentalStdlibApi"
            )
        }
    }
    idea {
        module {
            jdkName = "1.8"
            outputDir = file("$buildDir/classes/kotlin/main")
            testOutputDir = file("$buildDir/classes/kotlin/test")
            iml {
                @Suppress("RedundantSamConstructor")
                beforeMerged(Action<Module>{
                    dependencies.clear()
                })
            }
        }
    }
}

repositories {
    jcenter()
    mavenCentral()
}
tasks["sonarqube"].group = "verification"
tasks.named<Test>("test") {
    if (System.getenv("TRAVIS_BUILD_DIR") == null) {
        println("NOT UNDER TRAVIS PARALLEL TEST EXECUTION")
    } else {
        println("UNDER TRAVIS NON PARALLEL TEST EXECUTION")
        maxParallelForks = 1
    }
}

jacoco {
    toolVersion = "0.8.4"
}

task<JacocoReport>("codeCoverageReport") {
    group = "verification"

    executionData.setFrom(fileTree(project.rootDir.absolutePath).apply {
        include("**/build/jacoco/*.exec")
    })

    subprojects {
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
