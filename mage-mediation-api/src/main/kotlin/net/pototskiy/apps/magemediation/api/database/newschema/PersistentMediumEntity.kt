package net.pototskiy.apps.magemediation.api.database.newschema

import net.pototskiy.apps.magemediation.api.database.mdtn.MediumDataState
import net.pototskiy.apps.magemediation.api.database.mdtn.MediumDataTarget
import net.pototskiy.apps.magemediation.api.database.source.PersistentEntity
import org.jetbrains.exposed.dao.EntityID

open class PersistentMediumEntity(id: EntityID<Int>): PersistentEntity<PersistentMediumEntity>(id) {
    var target: MediumDataTarget
        get() = (klass.table as PersistentMediumEntityTable).target.getValue(this, ::target)
        set(value) = (klass.table as PersistentMediumEntityTable).target.setValue(this, ::target, value)
    var state: MediumDataState
        get() = (klass.table as PersistentMediumEntityTable).state.getValue(this, ::state)
        set(value) = (klass.table as PersistentMediumEntityTable).state.setValue(this, ::state, value)
}
