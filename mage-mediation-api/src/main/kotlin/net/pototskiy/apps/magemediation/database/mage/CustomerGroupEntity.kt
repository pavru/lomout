package net.pototskiy.apps.magemediation.database.mage

import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
import org.jetbrains.exposed.dao.EntityID

abstract class CustomerGroupEntity(id: EntityID<Int>) : SourceDataEntity(id) {
    abstract var customerGroupID: Long
    abstract var customerGroupCode: String
    abstract var taxClassID: Long
}