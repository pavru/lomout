package net.pototskiy.apps.magemediation.api.source

import net.pototskiy.apps.magemediation.api.AppConfigException
import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigBuildHelper
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.get
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

@Suppress("MagicNumber")
internal class HeaderReaderKtTest {
    private val typeManager = EntityTypeManager()
    private val helper = ConfigBuildHelper(typeManager)

    @Test
    internal fun createCorrectConfigurationTest() {
        createConfiguration()
        assertThat(typeManager["entity1"].attributes).hasSize(4)
        assertThat(typeManager["entity1"].attributes.map { it.name })
            .containsExactlyElementsOf(listOf("h1", "h2", "h3", "h4"))
    }

    @Test
    internal fun differentHeadersNumberTest() {
        assertThatThrownBy { createConfigurationDifferentHeadersNumber() }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Sources have different number of fields")
    }

    @Test
    internal fun differentHeadersOrderTest() {
        assertThatThrownBy { createConfigurationDifferentHeadersOrder()}
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Sources have different fields or fields in different columns")
    }

    private fun createConfiguration() = Config.Builder(helper).apply {
        database {
            server {
                host("localhost")
                port(3306)
                user("root")
                password(if (System.getenv("TRAVIS_BUILD_DIR") != null) "" else "root")
            }
        }
        loader {
            files {
                val testDataDir = System.getenv("TEST_DATA_DIR") ?: "../testdata"
                file("test-data") { path("$testDataDir/headers-from-source-test.xls") }
            }
            entities {
                entity("entity1", true) {}
            }
            loadEntity("entity1") {
                headersRow(0)
                fromSources {
                    source { file("test-data"); sheet("Sheet1"); }
                    source { file("test-data"); sheet("Sheet2"); }
                }
                sourceFields {
                    main("main") {
                        field("h1")
                    }
                }
            }
        }
    }.build()

    private fun createConfigurationDifferentHeadersNumber() = Config.Builder(helper).apply {
        database {
            server {
                host("localhost")
                port(3306)
                user("root")
                password(if (System.getenv("TRAVIS_BUILD_DIR") != null) "" else "root")
            }
        }
        loader {
            files {
                val testDataDir = System.getenv("TEST_DATA_DIR") ?: "../testdata"
                file("test-data") { path("$testDataDir/headers-from-source-test.xls") }
            }
            entities {
                entity("entity1", true) {}
            }
            loadEntity("entity1") {
                headersRow(0)
                fromSources {
                    source { file("test-data"); sheet("Sheet1"); }
                    source { file("test-data"); sheet("Sheet3"); }
                }
                sourceFields {
                    main("main") {
                        field("h1")
                    }
                }
            }
        }
    }.build()

    private fun createConfigurationDifferentHeadersOrder() = Config.Builder(helper).apply {
        database {
            server {
                host("localhost")
                port(3306)
                user("root")
                password(if (System.getenv("TRAVIS_BUILD_DIR") != null) "" else "root")
            }
        }
        loader {
            files {
                val testDataDir = System.getenv("TEST_DATA_DIR") ?: "../testdata"
                file("test-data") { path("$testDataDir/headers-from-source-test.xls") }
            }
            entities {
                entity("entity1", true) {}
            }
            loadEntity("entity1") {
                headersRow(0)
                fromSources {
                    source { file("test-data"); sheet("Sheet1"); }
                    source { file("test-data"); sheet("Sheet4"); }
                }
                sourceFields {
                    main("main") {
                        field("h1")
                    }
                }
            }
        }
    }.build()
}
