package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.sql.ReferenceOption

object CategoryDoubleTable: TypedAttributeTable<Double>("mdtn_cat_double") {
    override val owner = reference("category", MediumCategories, ReferenceOption.CASCADE)
    override val value = double("value")
}