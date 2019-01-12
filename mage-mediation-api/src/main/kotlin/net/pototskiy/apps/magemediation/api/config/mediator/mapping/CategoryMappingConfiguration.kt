package net.pototskiy.apps.magemediation.api.config.mediator.mapping

class CategoryMappingConfiguration (
    val mageIDToOnecID: Map<Any, Any>,
    val mageIDtoOnecPath: Map<Any,String>,
    val onecIDToMageID: Map<Any, Any>,
    val onecIDToMagePath: Map<Any, String>,
    val magePathToOnecPath: Map<String, String>,
    val magePathToOnecID: Map<String, Any>,
    val onecPathToMagePath: Map<String, String>,
    val onecPathToMageID: Map<String,Any>
)
