package net.pototskiy.apps.magemediation.api.config.resolver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable

internal class LocalMavenRepositoryTest {
    @Test
    @DisabledIfEnvironmentVariable(named = "TRAVIS_BUILD_DIR", matches = ".*")
    internal fun homeFolderTest() {
        assertThat(LocalMavenRepository.findLocalMavenRepo()).isNotNull()
        assertThat(localMaven().string).isNotBlank()
    }
}
