class RelationGroupNameFromGroup : AttributeBuilderPlugin<STRING>() {
    override fun build(entity: Entity): STRING? {
        return entity[relationGroupCodeAttr]?.let { code ->
            repository.get(groupEntityType, mapOf(groupCodeAttr to code))?.let { group ->
                return group[groupNameAttr] as? STRING
            }
        }
    }

    companion object {
        //        private const val relationEntityType = "onec-group-relation"
//        private const val groupEntityType = "onec-group"
        private val typeManager by lazy { PluginContext.entityTypeManager }
        private val relationEntityType by lazy { typeManager["onec-group-relation"] }
        private val groupEntityType by lazy { typeManager["onec-group"] }
        private val relationGroupCodeAttr by lazy {
            typeManager.getEntityAttribute(relationEntityType, "group_code")
                ?: throw AppConfigException(
                    badPlace(relationEntityType),
                    "Attribute 'group_code' is not defined."
                )
        }
        private val groupCodeAttr by lazy {
            typeManager.getEntityAttribute(groupEntityType, "group_code")
                ?: throw AppConfigException(
                    badPlace(groupEntityType),
                    "Attribute 'group_code' is not defined."
                )
        }
        private val groupNameAttr by lazy {
            typeManager.getEntityAttribute(groupEntityType, "group_name")
                ?: throw AppConfigException(badPlace(groupEntityType), "Attribute 'group_name' is not defined.")
        }
    }
}
