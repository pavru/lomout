@file:Import("../reader/OnecGroupToLong.plugin.conf.kts")

import OnecGroupToLong_plugin_conf.OnecGroupToLong

class OnecProduct : Document() {
    @Key
    var sku: String = ""
    var weight: Double = 0.0
    @Reader(OnecGroupToLong::class)
    var group_code: Long = 0L
    var group_name: String? = null
    var catalog_sku: String? = null
    var russian_name: String? = null
    var english_name: String? = null
    var manufacturer: String? = null
    var country_of_manufacture: String? = null
    var machine_vendor: String? = null
    var machine: String? = null


    companion object : DocumentMetadata(OnecProduct::class)
}