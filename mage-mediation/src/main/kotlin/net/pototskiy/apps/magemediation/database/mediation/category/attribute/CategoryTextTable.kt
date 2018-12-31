package net.pototskiy.apps.magemediation.database.mediation.category.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories

object CategoryTextTable : TypedAttributeTable<String>(
    "mdtn_cat_text",
    MediumCategories,
    { text("value") }
)
