package net.pototskiy.apps.magemediation.api.config.mediator.onec

import net.pototskiy.apps.magemediation.api.config.type.AttributeType
import net.pototskiy.apps.magemediation.api.plugable.medium.GroupPathBuilder
import kotlin.reflect.KClass


class OnecGroupPathAttribute(
    val name: String,
    val type: AttributeType,
    val separator: Pair<String,String>?,
    val root: String?,
    val synthetic: Boolean = false,
    val builder: KClass<out GroupPathBuilder>? = null
)
