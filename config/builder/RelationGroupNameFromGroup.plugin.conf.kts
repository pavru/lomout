class RelationGroupNameFromGroup : AttributeBuilderPlugin<StringType>() {
    override fun build(entity: DbEntity): StringType? {
        return entity.readAttribute(relationGroupCodeAttr)?.let { code ->
            DbEntity.getByAttribute(groupEntityType, groupCodeAttr, code).firstOrNull()?.let { group ->
                return group.readAttribute(groupNameAttr) as? StringValue
            }
        }
    }

    companion object {
        //        private const val relationEntityType = "onec-group-relation"
//        private const val groupEntityType = "onec-group"
        private val relationEntityType by lazy { EntityTypeManager["onec-group-relation"] }
        private val groupEntityType by lazy { EntityTypeManager["onec-group"] }
        private val relationGroupCodeAttr by lazy {
            EntityTypeManager.getEntityAttribute(relationEntityType, "group_code")
                ?: throw PluginException("Attribute<$relationEntityType:group_code> is not defined")
        }
        private val groupCodeAttr by lazy {
            EntityTypeManager.getEntityAttribute(groupEntityType, "group_code")
                ?: throw PluginException("Attribute<$groupEntityType:group_code> is not defined")
        }
        private val groupNameAttr by lazy {
            EntityTypeManager.getEntityAttribute(groupEntityType, "group_name")
                ?: throw PluginException("Attribute<$groupEntityType:group_name> is not defined")
        }
    }
}