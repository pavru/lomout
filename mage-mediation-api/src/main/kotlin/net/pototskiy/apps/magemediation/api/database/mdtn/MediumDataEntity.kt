package net.pototskiy.apps.magemediation.api.database.mdtn

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

abstract class MediumDataEntity(id: EntityID<Int>) : IntEntity(id) {
    var target: MediumDataTarget
        get() = (klass.table as MediumDataTable).target.getValue(this, ::target)
        set(value) = (klass.table as MediumDataTable).target.setValue(this, ::target, value)
    var state: MediumDataState
        get() = (klass.table as MediumDataTable).state.getValue(this, ::state)
        set(value) = (klass.table as MediumDataTable).state.setValue(this, ::state, value)
}