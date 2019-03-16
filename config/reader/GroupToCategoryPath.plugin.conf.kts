class GroupToCategoryPath : AttributeReaderPlugin<StringType>() {
    override fun read(attribute: Attribute<out StringType>, input: Cell): StringType? {
        try {
            val extendedInfo = entityTypeManager["onec-group-extended"]
            val groupId = input.longValue
            val entity = DbEntity.getByAttribute(
                extendedInfo,
                extendedInfo.getAttributeOrNull("group_code")
                    ?: throw AppPluginException("Attribute<group_code> is not defined in entity<onec-group-extended>"),
                LongType(groupId)
            ).firstOrNull()
            return entity?.readAttribute(
                extendedInfo.getAttributeOrNull("magento_path")
                    ?: throw AppPluginException("Attribute<magento_path is not defined in entity<onec-group-extended>")
            ) as? StringType?
        } catch (e: Exception) {
            throw AppPluginException(e.message, e)
        }
    }
}
