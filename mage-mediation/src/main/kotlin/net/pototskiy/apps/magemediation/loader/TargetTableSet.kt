package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.config.excel.Field
import net.pototskiy.apps.magemediation.config.excel.FieldType
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.source.SourceDataEntityClass
import net.pototskiy.apps.magemediation.database.source.SourceDataTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class TargetTableSet(
    val entity: SourceDataEntityClass<*>,
    val intAttributes: TypedAttributeEntityClass<Long, *>? = null,
    val doubleAttributes: TypedAttributeEntityClass<Double, *>? = null,
    val boolAttributes: TypedAttributeEntityClass<Boolean, *>? = null,
    val varcharAttributes: TypedAttributeEntityClass<String, *>? = null,
    val textAttributes: TypedAttributeEntityClass<String, *>? = null,
    val dateAttributes: TypedAttributeEntityClass<DateTime, *>? = null,
    val datetimeAttributes: TypedAttributeEntityClass<DateTime, *>? = null
) {
    val mainTableHeaders: List<Column<*>>
        get() {
            val table = entity.table as SourceDataTable
            return entity.table.columns.filter {
                it.name != entity.table.id.name
                        && it.name != table.createdInMedium.name
                        && it.name != table.updatedInMedium.name
                        && it.name != table.absentDays.name
            }
        }

    fun isKeyFiledTypeCompatible(column: Column<*>, field: Field): Boolean =
        transaction {
            when {
                column.columnType.sqlType().replace(Regex("\\(.+\\)"), "")
                        == VarCharColumnType().sqlType().replace(Regex("\\(.*\\)"), "")
                        && field.type == FieldType.STRING -> true
                column.columnType.sqlType() == LongColumnType().sqlType()
                        && field.type == FieldType.INT -> true
                column.columnType.sqlType() == DoubleColumnType().sqlType()
                        && field.type == FieldType.DOUBLE -> true
                column.columnType.sqlType() == TextColumnType().sqlType()
                        && field.type == FieldType.TEXT -> true
                column.columnType.sqlType() == BooleanColumnType().sqlType()
                        && field.type == FieldType.BOOL -> true
                column.columnType.sqlType() == DateColumnType(false).sqlType()
                        && field.type == FieldType.DATE -> true
                column.columnType.sqlType() == DateColumnType(true).sqlType()
                        && field.type == FieldType.DATETIME -> true
                else -> false
            }
        }
}