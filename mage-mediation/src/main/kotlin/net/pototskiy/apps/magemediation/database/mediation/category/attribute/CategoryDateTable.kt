package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import org.joda.time.DateTime

object CategoryDateTable : TypedAttributeTable<DateTime>(
    "mdtn_cat_bool",
    MediumCategories,
    { date("value") }
)