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

package net.pototskiy.apps.lomout.api.config.resolver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

internal class LocalMavenRepositoryTest {
    @Test
    @DisabledIfEnvironmentVariable(named = "TRAVIS_BUILD_DIR", matches = ".*")
    internal fun homeFolderTest() {
        @Suppress("UsePropertyAccessSyntax")
        assertThat(LocalMavenRepository.findLocalMavenRepo()).isNotNull()
        @Suppress("UsePropertyAccessSyntax")
        assertThat(localMaven().string).isNotBlank()
    }
    @Test
    @EnabledIfEnvironmentVariable(named = "TRAVIS_BUILD_DIR", matches = ".*")
    internal fun homeFolderCITest() {
        assertThat(LocalMavenRepository.findLocalMavenRepo()).isNull()
    }
}
