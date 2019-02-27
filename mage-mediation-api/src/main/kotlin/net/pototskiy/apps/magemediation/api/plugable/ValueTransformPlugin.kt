package net.pototskiy.apps.magemediation.api.plugable

abstract class ValueTransformPlugin<T, R> : Plugin() {
    abstract fun transform(value: T): R
}

typealias ValueTransformFunction<T, R> = (T) -> R
