package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.sql.ReferenceOption

object CategoryIntTable : TypedAttributeTable<Long>("mdtn_cat_int") {
    override val owner = reference("category", MediumCategories, ReferenceOption.CASCADE)
    override val value = long("value")
}
