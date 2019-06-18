class GroupToCategoryPath : AttributeBuilderPlugin<STRING>() {
    override fun build(entity: Entity): STRING? {
        try {
            val extendedInfo = entityTypeManager["onec-group-extended"]
            val groupId = entity["group_code"] as LONG
            val entityExtInfo = repository.get(
                extendedInfo,
                mapOf(
                    (extendedInfo.getAttributeOrNull("group_code")
                        ?: throw AppConfigException(
                            badPlace(entity.type),
                            "Attribute 'group_code' is not defined."
                        )) to groupId
                ),
                EntityStatus.CREATED, EntityStatus.UPDATED, EntityStatus.UNCHANGED
            )
            return entityExtInfo?.get(
                extendedInfo.getAttributeOrNull("magento_path")
                    ?: throw AppConfigException(
                        badPlace(entityExtInfo.type),
                        "Attribute 'magento_path' is not defined."
                    )
            ) as? STRING?
        } catch (e: Exception) {
            throw AppConfigException(badPlace(entity.type), e.message, e)
        }
    }
}
