class MarkCategoryToRemove : PipelineAssemblerPlugin() {
    override fun assemble(target: EntityType, entities: PipelineDataCollection): Map<AnyTypeAttribute, Type?> {
        try {
            val category = entities["mage-category"]
            val idAttr = category.findAttribute("entity_id")
                ?: throw PluginException("Can not find attribute<entity_id>")
            val removeAttr = target.getAttributeOrNull("remove_flag")
                ?: throw PluginException("Can not find attribute<remove_flag>")
            return mapOf(
                idAttr to category["entity_id"],
                removeAttr to BooleanType(true)
            )
        } catch (e: Exception) {
            throw PluginException(e.message, e)
        }
    }
}
