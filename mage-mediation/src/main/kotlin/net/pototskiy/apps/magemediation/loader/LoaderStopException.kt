package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.loader.LoaderException

@Suppress("unused")
class LoaderStopException: LoaderException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace
    )
}
