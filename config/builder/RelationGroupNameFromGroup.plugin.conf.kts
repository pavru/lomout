import OnecGroupRelation_conf.OnecGroupRelation
import OnecGroup_conf.OnecGroup
import org.litote.kmongo.eq

class RelationGroupNameFromGroup : AttributeBuilder<String?>() {
    override fun build(entity: Document): String? {
        entity as OnecGroupRelation
        return repository.get(
            OnecGroup::class,
            OnecGroup::group_code eq entity.group_code
        )?.let { (it as OnecGroup).group_name }
    }
}
