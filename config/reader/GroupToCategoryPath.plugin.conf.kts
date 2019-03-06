class GroupToCategoryPath : AttributeReaderPlugin<StringType>() {
    override fun read(attribute: Attribute<out StringType>, input: Cell): StringType? {
        try {
            val extendedInfo = EntityTypeManager["onec-group-extended"]
            val groupId = input.longValue
            val entity = DbEntity.getByAttribute(
                extendedInfo,
                extendedInfo.getAttributeOrNull("group_code")
                    ?: throw PluginException("Attribute<group_code> is not defined in entity<onec-group-extended>"),
                LongValue(groupId)
            ).firstOrNull()
            return entity?.readAttribute(
                extendedInfo.getAttributeOrNull("magento_path")
                    ?: throw PluginException("Attribute<magento_path is not defined in entity<onec-group-extended>")
            ) as? StringType?
        } catch (e: Exception) {
            throw PluginException(e.message, e)
        }
    }
}
