package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.sql.ReferenceOption

object CategoryVarcharTable : TypedAttributeTable<String>("mdtn_cat_varchar") {
    override val owner = reference("category", MediumCategories, ReferenceOption.CASCADE)
    override val value = varchar("value", 300)
}
