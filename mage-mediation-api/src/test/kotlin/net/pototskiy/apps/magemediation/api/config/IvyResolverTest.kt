package net.pototskiy.apps.magemediation.api.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class IvyResolverTest {

    @Test
    fun localResolveTest() {
        val resolver = IvyResolver()
        resolver.tryAddRepository(localMaven())
        val deps = resolver.tryResolve("oooast-tools:mage-mediation-api:latest.integration")
        assertThat(deps).isNotEmpty
    }

    @Test
    fun remoteResolveTest() {
        val resolver = IvyResolver()
        resolver.tryAddRepository("https://repo.maven.apache.org/maven2/")
        val deps = resolver.tryResolve("org.jetbrains.kotlin:kotlin-stdlib:latest.integration")
        assertThat(deps).isNotEmpty
    }
}
