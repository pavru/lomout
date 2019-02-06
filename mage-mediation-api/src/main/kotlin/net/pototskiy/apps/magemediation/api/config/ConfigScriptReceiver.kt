package net.pototskiy.apps.magemediation.api.config

class ConfigScriptReceiver(args: Array<Any?> = emptyArray()) {
    init {
        args.forEach { println(it) }
    }
    var config: Config? = null

    @Suppress("unused")
    fun ConfigScriptReceiver.config(block: Config.Builder.() -> Unit) {
        config = Config.Builder().apply(block).build()
    }
}
