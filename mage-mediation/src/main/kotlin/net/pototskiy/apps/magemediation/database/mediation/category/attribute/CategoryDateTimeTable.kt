package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime

object CategoryDateTimeTable : TypedAttributeTable<DateTime>("mdtn_cat_datetime") {
    override val owner = reference("category", MediumCategories, ReferenceOption.CASCADE)
    override val value = datetime("value")
}