package net.pototskiy.apps.magemediation.api.plugable

abstract class NewValueTransformPlugin<T, R> : NewPlugin<R>() {
    @Suppress("MemberVisibilityCanBePrivate")
    protected abstract var value: T

    fun transform(value: T): R {
        this.value = value
        return execute()
    }
}

typealias NewValueTransformFunction<T, R> = (T) -> R
