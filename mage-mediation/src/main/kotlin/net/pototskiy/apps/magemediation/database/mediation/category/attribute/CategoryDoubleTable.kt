package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories

object CategoryDoubleTable : TypedAttributeTable<Double>(
    "mdtn_cat_double",
    MediumCategories,
    { double("value") }
)