package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.joda.time.DateTime

class CategoryDateTime(id: EntityID<Int>): TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, CategoryDateTime>(CategoryDateTimeTable)

    override var owner: IntEntity by MediumCategory referencedOn CategoryDateTimeTable.owner
    override var index by CategoryDateTimeTable.index
    override var code by CategoryDateTimeTable.code
    override var value by CategoryDateTimeTable.value
}