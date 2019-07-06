@file:Import("../reader/OnecGroupToLong.plugin.conf.kts")
@file:Import("../builder/GroupPathFromRelation.plugin.conf.kts")
@file:Import("../builder/GroupToCategoryPath.plugin.conf.kts")

import GroupPathFromRelation_plugin_conf.GroupPathFromRelation
import GroupToCategoryPath_plugin_conf.GroupToCategoryPath
import OnecGroupToLong_plugin_conf.OnecGroupToLong
import org.bson.codecs.pojo.annotations.BsonIgnore

class OnecGroup : Document() {
    @Key
    @Reader(OnecGroupToLong::class)
    var group_code: Long = 0L
    var group_name: String = ""
    @get: BsonIgnore
    val __path: String by lazy { pathBuilder.build(this)!! }
    val entity_id: Long
        get() = group_code
    @get:BsonIgnore
    val transformed_path: String by lazy { transformedPathBuilder.build(this)!! }

    companion object : DocumentMetadata(OnecGroup::class) {
        val transformedPathBuilder = GroupToCategoryPath()
        val pathBuilder = GroupPathFromRelation("/","/Root Catalog/Default Category/Каталог/")
    }
}