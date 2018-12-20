package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.config.excel.Field
import net.pototskiy.apps.magemediation.config.excel.FieldType
import net.pototskiy.apps.magemediation.database.*
import net.pototskiy.apps.magemediation.database.attribute.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TargetTableSet(
    val entity: VersionEntityClass<*>,
    val intAttributes: IntAttributeEntityClass<*>,
    val doubleAttributes: DoubleAttributeEntityClass<*>,
    val boolAttributes: BoolAttributeEntityClass<*>,
    val varcharAttributes: VarcharAttributeEntityClass<*>,
    val textAttributes: TextAttributeEntityClass<*>,
    val dateAttributes: DateAttributeEntityClass<*>,
    val datetimeAttributes: DatetimeAttributeEntityClass<*>
) {
    val mainTableHeaders: List<Column<*>>
        get() {
            entity.table as VersionTable
            return entity.table.columns.filter {
                it.name != entity.table.id.name
                        && it.name != entity.table.createdInMedium.name
                        && it.name != entity.table.updatedInMedium.name
                        && it.name != entity.table.absentDays.name
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