class GroupToCategoryPath : AttributeReaderPlugin<StringType>() {
    override fun read(attribute: Attribute<out StringType>, input: Cell): StringType? {
        try {
            val extendedInfo = entityTypeManager["onec-group-extended"]
            val groupId = input.longValue
            val entity = DbEntity.getByAttribute(
                extendedInfo,
                extendedInfo.getAttributeOrNull("group_code")
                    ?: throw AppConfigException(badPlace(attribute) + input, "Attribute 'group_code' is not defined."),
                LongType(groupId)
            ).firstOrNull()
            return entity?.readAttribute(
                extendedInfo.getAttributeOrNull("magento_path")
                    ?: throw AppConfigException(badPlace(attribute) + input, "Attribute 'magento_path' is not defined.")
            ) as? StringType?
        } catch (e: Exception) {
            throw AppConfigException(badPlace(attribute) + input, e.message, e)
        }
    }
}
