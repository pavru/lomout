package net.pototskiy.apps.magemediation.mediator

import net.pototskiy.apps.magemediation.api.config.DatabaseConfig
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.database.initDatabase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@Suppress("MagicNumber")
internal class MediatorBasicTest {
    private val typeManager = EntityTypeManager()
    @BeforeEach
    internal fun setUp() {
        val dbConfig = DatabaseConfig.Builder("test_magemediation").apply {
            server {
                host("localhost")
                port(3306)
                user("root")
                password("root")
            }
        }.build()
        initDatabase(dbConfig, typeManager)
    }

    @AfterEach
    internal fun tearDown() {
        // nothing to clean up
    }
}
