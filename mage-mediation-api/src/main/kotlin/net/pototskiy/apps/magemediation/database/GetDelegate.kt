package net.pototskiy.apps.magemediation.database

import kotlin.reflect.KProperty

fun getDelegate(obj: Any, property: KProperty<*>): Any? {
    return obj::class.java.declaredFields
        .find { it.name ==  "${property.name}${'$'}delegate" }
        ?.apply { isAccessible = true }
        ?.get(obj)
}