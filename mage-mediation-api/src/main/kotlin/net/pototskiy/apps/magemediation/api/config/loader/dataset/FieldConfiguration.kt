package net.pototskiy.apps.magemediation.api.config.loader.dataset

import net.pototskiy.apps.magemediation.api.config.type.AttributeType
import net.pototskiy.apps.magemediation.api.plugable.loader.FieldTransformer
import kotlin.reflect.KClass


data class FieldConfiguration(
    val name: String,
    val column: Int,
    val regex: Regex?,
    val type: AttributeType,
    val keyField: Boolean,
    val nested: Boolean,
    val parent: String?,
    val optional: Boolean,
    val transformer: KClass<out FieldTransformer<out Any>>? = null
)