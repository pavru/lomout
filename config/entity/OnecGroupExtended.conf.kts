@file:Import("../reader/OnecGroupToLong.plugin.conf.kts")

import OnecGroupToLong_plugin_conf.OnecGroupToLong

class OnecGroupExtended : Document() {
    @Key
    @Reader(OnecGroupToLong::class)
    var group_code: Long = 0L
    var group_name: String? = null
    var magento_path: String? = null
    var url: String? = null
    var description: String? = null

    companion object : DocumentMetadata(OnecGroupExtended::class)
}
