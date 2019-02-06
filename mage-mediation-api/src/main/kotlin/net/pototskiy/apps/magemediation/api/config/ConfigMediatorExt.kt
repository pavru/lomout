package net.pototskiy.apps.magemediation.api.config

fun Config.mapCategoryIDToGroupID(mageID: Any?): Any? = mageID?.let {
    this.mediator.mapping.category.mageIDToOnecID[it]
}

fun Config.mapGroupIDToCategoryID(onecID: Any?): Any? = onecID?.let {
    this.mediator.mapping.category.onecIDToMageID[it]
}
