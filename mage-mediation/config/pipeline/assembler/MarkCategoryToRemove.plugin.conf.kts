class MarkCategoryToRemove : PipelineAssemblerPlugin() {
    override fun assemble(target: EType, entities: PipelineDataCollection): Map<AnyTypeAttribute, Type?> {
        val category = entities["mage-category"]
            ?: throw PluginException("Can not find entity<mage-category>")
        val idAttr = category.findAttribute("entity_id")
            ?: throw PluginException("Can not find attribute<entity_id>")
        val removeAttr = target.findAttribute("remove_flag")
            ?: throw PluginException("Can not find attribute<remove_flag>")
        return mapOf(
            idAttr to category["entity_id"],
            removeAttr to BooleanValue(true)
        )
    }
}
