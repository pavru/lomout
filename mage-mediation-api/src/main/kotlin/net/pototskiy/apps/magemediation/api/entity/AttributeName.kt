package net.pototskiy.apps.magemediation.api.entity

data class AttributeName(
    val entityType: String,
    val attributeName: String
) {
    val fullName: String by lazy { "$entityType:$attributeName" }
    override fun toString(): String = fullName
}
