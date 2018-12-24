package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.sql.ReferenceOption

object CategoryTextTable : TypedAttributeTable<String>("mdtn_cat_text") {
    override val owner = reference("category", MediumCategories, ReferenceOption.CASCADE)
    override val value = text("value")
}
