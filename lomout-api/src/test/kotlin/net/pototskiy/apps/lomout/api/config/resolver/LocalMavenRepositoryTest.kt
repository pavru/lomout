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
