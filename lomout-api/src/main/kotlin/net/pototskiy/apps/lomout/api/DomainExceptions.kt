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
     * Exception place
     */
    val place: DomainExceptionPlace

    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace) : super() {
        this.place = place
    }

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace, message: String?) : super(message) {
        this.place = place
    }

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace, message: String?, cause: Throwable?) : super(message, cause) {
        this.place = place
    }
}

/**
 * Configuration problems related to entity type
 */
@Generated
open class AppDataException : AppException {
    /**
     * Exception place
     */
    val place: DomainExceptionPlace

    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace) : super() {
        this.place = place
    }

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace, message: String?) : super(message) {
        this.place = place
    }

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace, message: String?, cause: Throwable?) : super(message, cause) {
        this.place = place
    }
}
