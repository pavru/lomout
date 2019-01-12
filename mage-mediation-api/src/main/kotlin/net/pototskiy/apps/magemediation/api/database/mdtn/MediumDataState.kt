package net.pototskiy.apps.magemediation.api.database.mdtn

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

enum class MediumDataState {
    REMOVE, SKIP, CREATE, UPDATE
}

class MediumDataStateColumnType : ColumnType() {
    override fun sqlType(): String = "VARCHAR(10)"

    override fun valueFromDB(value: Any): Any {
        return MediumDataState.valueOf(value as String)
    }

    override fun valueToDB(value: Any?): Any? {
        return (value as? MediumDataState)?.name
    }
}

fun Table.mediumDataState(name: String): Column<MediumDataState> =
    registerColumn(name, MediumDataStateColumnType())