package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppDatabaseException
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class EntityTypeColumnTypeTest {
    private val typeManager = EntityTypeManagerImpl()
    private val entity1 = typeManager.createEntityType("entity1", emptyList(), false)
    private val entity2 = typeManager.createEntityType("entity2", emptyList(), false)

    @Test
    internal fun valueFromDbTest() {
        DbEntityTable.entityTypeManager = typeManager
        val type = EntityTypeColumnType(DbEntityTable)
        assertThat(type.valueFromDB(entity1)).isEqualTo(entity1)
        assertThat(type.valueFromDB("entity1")).isEqualTo(entity1)
        assertThatThrownBy { type.valueFromDB("entity3") }
            .isInstanceOf(AppConfigException::class.java)
            .hasMessageContaining("Undefined entity type.")
        assertThatThrownBy { type.valueFromDB(1L) }
            .isInstanceOf(AppDatabaseException::class.java)
            .hasMessageContaining("Unexpected value '1' of type '${Long::class.qualifiedName}'.")
    }

    @Test
    internal fun valueToDbTest() {
        DbEntityTable.entityTypeManager = typeManager
        val type = EntityTypeColumnType(DbEntityTable)
        type.nullable = true
        assertThat(type.valueToDB(null)).isNull()
        type.nullable = false
        assertThatThrownBy { type.valueToDB(null) }
            .isInstanceOf(AppDatabaseException::class.java)
            .hasMessageContaining("Null in non-nullable column")
        assertThat(type.valueToDB(entity2)).isEqualTo("entity2")
        assertThat(type.valueToDB("entity2")).isEqualTo("entity2")
        assertThatThrownBy { type.valueFromDB(1L) }
            .isInstanceOf(AppDatabaseException::class.java)
            .hasMessageContaining("Unexpected value '1' of type '${Long::class.qualifiedName}'.")
    }
}
