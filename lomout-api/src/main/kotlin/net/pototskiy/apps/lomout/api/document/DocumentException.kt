package net.pototskiy.apps.lomout.api.document

/**
 * Document exception
 */
class DocumentException : Exception {
    /**
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     * @param message The message
     * @constructor
     */
    constructor(message: String?) : super(message)

    /**
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    /**
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(cause: Throwable?) : super(cause)

    /**
     * @param message The message
     * @param cause The cause
     * @param enableSuppression Boolean
     * @param writableStackTrace Boolean
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace
    )
}
