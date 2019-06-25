class MarkCategoryToRemove : PipelineAssemblerPlugin() {
    override fun assemble(target: EntityType, entities: EntityCollection): Map<AnyTypeAttribute, Type> {
        try {
            val category = entities["mage-category"]
            val idAttr = category.type.getAttributeOrNull("entity_id")
                ?: throw AppConfigException(badPlace(target), "Cannot find attribute 'entity_id'.")
            val removeAttr = target.getAttributeOrNull("remove_flag")
                ?: throw AppConfigException(badPlace(target), "Cannot find attribute 'remove_flag'.")
            return mapOf(
                idAttr to category["entity_id"]!!,
                removeAttr to BOOLEAN(true)
            )
        } catch (e: Exception) {
            throw AppConfigException(badPlace(target), e.message, e)
        }
    }
}
