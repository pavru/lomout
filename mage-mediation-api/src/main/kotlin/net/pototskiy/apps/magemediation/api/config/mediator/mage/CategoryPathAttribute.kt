package net.pototskiy.apps.magemediation.api.config.mediator.mage

import net.pototskiy.apps.magemediation.api.config.type.AttributeType
import net.pototskiy.apps.magemediation.api.plugable.medium.CategoryPathBuilder
import kotlin.reflect.KClass

class CategoryPathAttribute(
    val name: String,
    val type: AttributeType,
    val separator: Pair<String,String>?,
    val root: String?,
    val synthetic: Boolean,
    val builder: KClass<out CategoryPathBuilder>? = null
)
