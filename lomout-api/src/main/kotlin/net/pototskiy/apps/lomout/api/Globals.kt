package net.pototskiy.apps.lomout.api

import java.util.*

/**
 * Root log name
 */
const val ROOT_LOG_NAME = "net.pototskiy.apps.lomout"
/**
 * Log name for configuration loading and compilation
 */
const val CONFIG_LOG_NAME = "net.pototskiy.apps.lomout.config"
/**
 * Database operation log name
 */
@Suppress("unused")
const val DATABASE_LOG_NAME = "net.pototskiy.apps.lomout.database"
/**
 * Loader log name
 */
const val LOADER_LOG_NAME = "net.pototskiy.apps.lomout.loader"
/**
 * Mediator log name
 */
const val MEDIATOR_LOG_NAME = "net.pototskiy.apps.lomout.mediator"
/**
 * Printer log name
 */
const val PRINTER_LOG_NAME = "net.pototskiy.apps.lomout.printer"
/**
 * Common information log name
 */
const val STATUS_LOG_NAME = "net.pototskiy.apps.lomout.status"
/**
 * Exposed log name
 */
const val EXPOSED_LOG_NAME = "Exposed"

/**
 * Undefined column number
 */
const val UNDEFINED_COLUMN = -1
/**
 * Undefined row number
 */
const val UNDEFINED_ROW = -1
/**
 * Default system locale
 */
val DEFAULT_LOCALE: Locale = Locale.getDefault()
/**
 * Default system locale in string format
 */
val DEFAULT_LOCALE_STR: String = DEFAULT_LOCALE.toString()

/**
 * CSV file sheet name
 */
const val CSV_SHEET_NAME = "default"

/**
 * Standard not implemented message
 */
@Suppress("unused")
const val NOT_IMPLEMENTED = "not implemented"
