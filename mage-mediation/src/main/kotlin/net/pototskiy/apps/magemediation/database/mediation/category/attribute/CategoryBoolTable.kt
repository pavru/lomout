package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories

object CategoryBoolTable : TypedAttributeTable<Boolean>(
    "medium_cat_bool",
    MediumCategories,
    { bool("value") }
)
