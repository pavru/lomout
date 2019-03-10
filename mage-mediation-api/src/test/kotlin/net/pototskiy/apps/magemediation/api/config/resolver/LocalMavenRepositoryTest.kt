package net.pototskiy.apps.magemediation.api.config.resolver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LocalMavenRepositoryTest {
    @Test
    internal fun homeFolderTest() {
        assertThat(LocalMavenRepository.findLocalMavenRepo()).isNotNull()
        assertThat(localMaven().string).isNotBlank()
    }
}
