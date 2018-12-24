package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategory
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

class CategoryText(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, CategoryText>(CategoryTextTable)

    override var owner: IntEntity by MediumCategory referencedOn CategoryTextTable.owner
    override var index by CategoryTextTable.index
    override var code by CategoryTextTable.code
    override var value by CategoryTextTable.value
}