package net.pototskiy.apps.lomout.api

/**
 * Application related exception
 */
@Generated
open class AppException : Exception {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Database (SQL problems) exception
 */
@Generated
open class AppDatabaseException : AppException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * All configuration problems
 */
@Generated
open class AppConfigException : AppException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Configuration problems related to entity type
 */
@Generated
open class AppEntityTypeException : AppConfigException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Configuration problems related to entity attributes
 */
@Generated
open class AppAttributeException : AppEntityTypeException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Problems related to source file or workbook
 */
@Generated
open class AppWorkbookException : AppException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Workbook sheet related problems
 */
@Generated
open class AppSheetException : AppWorkbookException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Sheet row related problems
 */
@Generated
open class AppRowException : AppSheetException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Cell related problems
 */
@Generated
open class AppCellException : AppRowException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Cell data related problems
 */
open class AppCellDataException : AppCellException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Data/Values (not in the cell) related problems
 */
@Generated
open class AppDataException : AppException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Plugin related problems, including internal plugin problem
 */
@Generated
open class AppPluginException : AppException {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
