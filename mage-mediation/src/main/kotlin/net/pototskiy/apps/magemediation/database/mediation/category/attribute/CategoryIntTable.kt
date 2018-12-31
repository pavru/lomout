package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories

object CategoryIntTable : TypedAttributeTable<Long>(
    "mdtn_cat_int",
    MediumCategories,
    { long("value") }
)
