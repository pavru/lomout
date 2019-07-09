package net.pototskiy.apps.lomout.api

import org.jetbrains.annotations.PropertyKey
import java.util.*

internal object MessageBundle {
    private val bundle = ResourceBundle.getBundle("messages_api", UTF8Control())

    @Suppress("SpreadOperator")
    @JvmStatic
    fun message(
        @PropertyKey(resourceBundle = "messages_api") key: String,
        vararg params: Any?
    ): String {
        return try {
            (bundle.getString(key) as String).let {
                if (params.isNotEmpty()) {
                    it.format(*params)
                } else {
                    it
                }
            }
        } catch (e: MissingResourceException) {
            "!$key!"
        }
    }
}
