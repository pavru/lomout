@file:Import("../builder/RelationGroupNameFromGroup.plugin.conf.kts")

import RelationGroupNameFromGroup_plugin_conf.RelationGroupNameFromGroup
import org.bson.codecs.pojo.annotations.BsonIgnore

class OnecGroupRelation : Document() {
    @Key
    var group_code: Long = 0L
    var group_parent_code: Long? = null
    @get:BsonIgnore
    val group_name: String by lazy { nameBuilder.build(this)!! }

    companion object : DocumentMetadata(OnecGroupRelation::class) {
        val nameBuilder = RelationGroupNameFromGroup()
    }
}