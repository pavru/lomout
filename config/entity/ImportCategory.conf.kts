@file:Import("MageCategory.conf.kts")

import MageCategory_conf.MageCategory

class ImportCategory : MageCategory() {
    var remove_flag: Boolean = false

    companion object : DocumentMetadata(ImportCategory::class)
}