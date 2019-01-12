package net.pototskiy.apps.magemediation.api.medium

import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.api.database.onec.GroupEntity

interface CategoryComparator {
    fun findMageCategoryByOnecCode(entityID: Long): CategoryEntity?
    fun findMageCategoryByOnecPath(path: String): CategoryEntity?
    fun findOnecGroupByMageEntityID(code: String): GroupEntity?
    fun findOnecGroupByMagePath(path: String): GroupEntity?

    fun isThereMageCategoryByGroupCode(entityID: Long) = findMageCategoryByOnecCode(entityID) != null
    fun isThereMageCategoryByPath(path: String) = findMageCategoryByOnecPath(path) != null
    fun isThereOnecGroupByEntityID(code: String) = findOnecGroupByMageEntityID(code) != null
    fun isThereOnecGroupByPath(path: String) = findOnecGroupByMagePath(path) != null

    fun mapOnecGroupToMageEntityID(groupCode: String): Long
    fun mapOnecPathToMagePath(path: String): String
    fun mapMageEntityIDToOnecGroup(entityID: Long): String
    fun mapMagePathToOnecPath(path: String): String
}