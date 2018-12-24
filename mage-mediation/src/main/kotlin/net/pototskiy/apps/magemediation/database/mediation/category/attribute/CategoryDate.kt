package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.joda.time.DateTime

class CategoryDate(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, CategoryDate>(CategoryDateTable)

    override var owner: IntEntity by MediumCategory referencedOn CategoryDateTable.owner
    override var index by CategoryDateTable.index
    override var code by CategoryDateTable.code
    override var value by CategoryDateTable.value
}