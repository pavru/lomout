package net.pototskiy.apps.magemediation.api.plugable.loader

interface FieldTransformer<T> {
    fun transform(value: T): T
}