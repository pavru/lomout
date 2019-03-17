package net.pototskiy.apps.magemediation.api.config

import net.pototskiy.apps.magemediation.api.NoExitSecurityManager
import net.pototskiy.apps.magemediation.api.createLocale
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

@Suppress("MagicNumber")
@DisplayName("Create config with loader section")
internal class ConfigLoaderPartTest {
    private lateinit var config: Config
    @BeforeEach
    internal fun setUp() {
        System.setSecurityManager(NoExitSecurityManager())
        config = ConfigurationBuilderFromDSL(
            File("${System.getenv("TEST_DATA_DIR")}/conf-test.conf.kts")
        ).config
    }

    @Test
    internal fun databaseConfigurationTest() {
        assertThat(config.database.name).isEqualTo("test_db_name")
        assertThat(config.database.server.host).isEqualTo("remote-host")
        assertThat(config.database.server.port).isEqualTo(3307)
        assertThat(config.database.server.user).isEqualTo("test-user")
        assertThat(config.database.server.password).isEqualTo("test-password")
    }

    @Test
    internal fun loaderFilesConfigurationTest() {
        assertThat(config.loader?.files).hasSize(9)
        assertThat(config.loader?.files?.map { it.file }).containsExactlyElementsOf(files)
        assertThat(config.loader?.files?.filter { it.locale == "ru_RU".createLocale() }).hasSize(1)
        assertThat(config.loader?.files?.filter { it.locale != "ru_RU".createLocale() }).hasSize(8)
    }

    @Test
    internal fun entitiesConfigurationTest() {
        assertThat(config.loader?.entities).hasSize(3)
        assertThat(config.loader?.entities?.map { it.name }).containsExactlyElementsOf(listOf(
            "test-entity-1", "test-entity-2", "onec-product"
        ))
        val entity1 = config.loader?.entities?.find { it.name == "test-entity-1" }
        assertThat(entity1).isNotNull
        assertThat(entity1?.attributes).hasSize(5)
        assertThat(entity1?.attributes?.map { it.name }).containsExactlyElementsOf(listOf(
            "string-attr-1", "text-attr-1", "boolean-attr-1", "long-attr-1", "double-attr-1"
            ))
    }

    companion object {
        private val files = listOf(
            File("${System.getenv("TEST_DATA_DIR")}/test.attributes.xls"),
            File("${System.getenv("TEST_DATA_DIR")}/test.attributes.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/test-products.xls"),
            File("${System.getenv("TEST_DATA_DIR")}/catalog_product.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/customer_group.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/catalog_category.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/customer_group.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/advanced_pricing.csv"),
            File("${System.getenv("TEST_DATA_DIR")}/stock_sources.csv")
        )
    }
}
