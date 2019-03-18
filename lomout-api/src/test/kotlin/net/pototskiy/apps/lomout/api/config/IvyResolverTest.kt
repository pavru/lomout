package net.pototskiy.apps.lomout.api.config

import net.pototskiy.apps.lomout.api.config.resolver.IvyResolver
import net.pototskiy.apps.lomout.api.config.resolver.jCenter
import net.pototskiy.apps.lomout.api.config.resolver.localMaven
import net.pototskiy.apps.lomout.api.config.resolver.mavenCentral
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class IvyResolverTest {

    @Test
    fun localResolveTest() {
        if (System.getenv("TRAVIS_BUILD_DIR") == null) {
            val resolver = IvyResolver()
            assertThat(resolver.tryAddRepository(localMaven())).isTrue()
            assertThat(resolver.tryAddRepository(jCenter())).isTrue()
            val deps = resolver.tryResolve("lomout:lomout-api:1.0-SNAPSHOT")
            assertThat(deps).isNotEmpty
            assertThat(deps?.filter {
                it.isFile && it.name == "lomout-api-1.0-SNAPSHOT.jar" }
            ).isNotEmpty
        } else {
            assertThat(true).isTrue()
        }
    }

    @Test
    fun remoteResolveTest() {
        val resolver = IvyResolver()
        assertThat(resolver.tryAddRepository(mavenCentral())).isTrue()
        var deps = resolver.tryResolve("org.jetbrains.kotlin:kotlin-stdlib:1.3.21")
        assertThat(deps).isNotEmpty
        deps = resolver.tryResolve("org.jetbrains.exposed:exposed:0.12.2")
        assertThat(deps).isNull()
        assertThat(
            resolver.tryAddRepository(jCenter())
        ).isTrue()
        deps = resolver.tryResolve("org.jetbrains.exposed:exposed:0.12.2")
        assertThat(deps).isNotEmpty
    }
}
