package net.pototskiy.apps.lomout.api

/**
 * Application related exception
 */
@Generated
open class AppException : Exception {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Database (SQL problems) exception
 */
@Generated
open class AppDatabaseException : AppException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * All configuration problems
 */
@Generated
open class AppConfigException : AppException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Configuration problems related to entity type
 */
@Generated
open class AppEntityTypeException : AppConfigException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Configuration problems related to entity attributes
 */
@Generated
open class AppAttributeException : AppEntityTypeException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Problems related to source file or workbook
 */
@Generated
open class AppWorkbookException : AppException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Workbook sheet related problems
 */
@Generated
open class AppSheetException : AppWorkbookException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Sheet row related problems
 */
@Generated
open class AppRowException : AppSheetException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Cell related problems
 */
@Generated
open class AppCellException : AppRowException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Cell data related problems
 */
open class AppCellDataException : AppCellException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * Data/Values (not in cell) related problems
 */
@Generated
open class AppDataException : AppException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

@Generated
open class AppPluginException : AppException {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(message: String?) : super(message)

    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
