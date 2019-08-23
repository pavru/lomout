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

package net.pototskiy.apps.lomout.api.script

import net.pototskiy.apps.lomout.api.script.resolver.IvyResolver
import net.pototskiy.apps.lomout.api.script.resolver.jCenter
import net.pototskiy.apps.lomout.api.script.resolver.localMaven
import net.pototskiy.apps.lomout.api.script.resolver.mavenCentral
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.script.util.resolvers.experimental.BasicArtifactCoordinates
import org.junit.jupiter.api.Test

internal class IvyResolverTest {

    @Test
    fun localResolveTest() {
        if (System.getenv("TRAVIS_BUILD_DIR") == null) {
            val resolver = IvyResolver()
            assertThat(resolver.tryAddRepository(localMaven())).isEqualTo(true)
            assertThat(resolver.tryAddRepository(jCenter())).isEqualTo(true)
            @Suppress("GraziInspection")
            val deps = resolver.tryResolve("lomout:lomout-api:1.0-SNAPSHOT")
            assertThat(deps).isNotEmpty
            assertThat(deps?.filter {
                it.isFile && it.name == "lomout-api-1.0-SNAPSHOT.jar" }
            ).isNotEmpty
        } else {
            assertThat(true).isEqualTo(true)
        }
    }

    @Test
    fun remoteResolveTest() {
        val resolver = IvyResolver()
        assertThat(resolver.tryAddRepository(mavenCentral())).isEqualTo(true)
        var deps = resolver.tryResolve("org.jetbrains.kotlin:kotlin-stdlib:1.3.21")
        assertThat(deps).isNotEmpty
        deps = resolver.tryResolve(BasicArtifactCoordinates("org.jetbrains.exposed:exposed:0.12.2"))
        assertThat(deps).isNull()
        assertThat(
            resolver.tryAddRepository(jCenter())
        ).isEqualTo(true)
        deps = resolver.tryResolve("org.jetbrains.exposed:exposed:0.12.2")
        assertThat(deps).isNotEmpty
    }
}
