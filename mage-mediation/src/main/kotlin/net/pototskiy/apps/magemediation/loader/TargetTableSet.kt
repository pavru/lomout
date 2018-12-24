package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.config.excel.Field
import net.pototskiy.apps.magemediation.config.excel.FieldType
import net.pototskiy.apps.magemediation.database.VersionEntityClass
import net.pototskiy.apps.magemediation.database.VersionTable
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class TargetTableSet(
    val entity: VersionEntityClass<*>,
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