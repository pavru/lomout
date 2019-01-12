package net.pototskiy.apps.magemediation.api.medium

interface CategoryRecordTools {
    fun createOnecGroupFromMageCategory(entityID: Long)
    fun createMageCategoryFromOnecGroup(code: String)
    fun updateOnecGroupFromMageCategory(entityID: Long, code: String)
    fun updateMageCategoryFromOnecGroup(code: String, entityID: Long)
    fun removeOnecGroup(code: String)
    fun removeMageCategory(entityID: Long)
    fun skipOnecGroup(code: String)
    fun skipMageCategory(code: String)
}