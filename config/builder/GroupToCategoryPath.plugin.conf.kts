
import OnecGroupExtended_conf.OnecGroupExtended
import OnecGroup_conf.OnecGroup
import org.litote.kmongo.eq

open class GroupToCategoryPath : AttributeBuilder<String?>() {
    override fun build(entity: Document): String? {
        try {
            entity as OnecGroup
            val extendedInfo = OnecGroupExtended::class
            val groupId = entity.group_code
            val entityExtInfo = repository.get(
                extendedInfo,
                OnecGroupExtended::group_code eq groupId
            ) as? OnecGroupExtended
            return entityExtInfo?.magento_path
        } catch (e: Exception) {
            throw AppConfigException(badPlace(entity::class), e.message, e)
        }
    }
}
