package net.pototskiy.apps.magemediation.database.mdtn

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

enum class MediumDataTarget {
    MAGE, ONEC
}

class MediumDataTargetColumnType : ColumnType() {
    override fun sqlType(): String = "VARCHAR(5)"

    override fun valueFromDB(value: Any): Any {
        return MediumDataTarget.valueOf(value as String)
    }

    override fun valueToDB(value: Any?): Any? {
        return (value as MediumDataTarget).name
    }
}

fun Table.mediumDataTarget(name: String): Column<MediumDataTarget> =
        registerColumn(name, MediumDataTargetColumnType())