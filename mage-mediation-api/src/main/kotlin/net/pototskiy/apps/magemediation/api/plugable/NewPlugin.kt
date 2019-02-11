package net.pototskiy.apps.magemediation.api.plugable

abstract class NewPlugin<R> {
    open fun setOptions(options: Options) = Unit
    open fun optionSetter(): Options = noOptions

    abstract fun execute(): R

    abstract class Options

    companion object {
        val noOptions = object : Options() {}
    }
}
