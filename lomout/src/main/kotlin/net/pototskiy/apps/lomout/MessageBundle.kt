package net.pototskiy.apps.lomout

import net.pototskiy.apps.lomout.api.UTF8Control
import org.jetbrains.annotations.PropertyKey
import java.util.*

internal object MessageBundle {
    private val bundle = ResourceBundle.getBundle("messages", UTF8Control())

    @Suppress("SpreadOperator")
    @JvmStatic
    fun message(
        @PropertyKey(resourceBundle = "messages") key: String,
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
