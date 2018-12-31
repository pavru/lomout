package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.joda.time.DateTime

object CategoryDateTimeTable : TypedAttributeTable<DateTime>(
    "mdtn_cat_datetime",
    MediumCategories,
    { datetime("value") }
)