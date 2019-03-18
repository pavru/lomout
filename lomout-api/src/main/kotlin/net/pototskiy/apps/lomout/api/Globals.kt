package net.pototskiy.apps.lomout.api

import org.joda.time.DateTime
import java.util.*

const val ROOT_LOG_NAME = "net.pototskiy.apps.lomout"
const val CONFIG_LOG_NAME = "net.pototskiy.apps.lomout.config"
const val DATABASE_LOG_NAME = "net.pototskiy.apps.lomout.database"
const val LOADER_LOG_NAME = "net.pototskiy.apps.lomout.loader"
const val MEDIATOR_LOG_NAME = "net.pototskiy.apps.lomout.mediator"
const val PRINTER_LOG_NAME = "net.pototskiy.apps.lomout.printer"
const val STATUS_LOG_NAME = "net.pototskiy.apps.lomout.status"
const val EXPOSED_LOG_NAME = "Exposed"

const val UNDEFINED_COLUMN = -1
const val UNDEFINED_ROW = -1
val DEFAULT_LOCALE: Locale = Locale.getDefault()
val DEFAULT_LOCALE_STR: String = DEFAULT_LOCALE.toString()
val TIMESTAMP = DateTime()

const val ENTITY_TYPE_NAME_LENGTH = 100

const val CSV_SHEET_NAME = "default"

// Standard strings
@Suppress("unused")
const val NOT_IMPLEMENTED = "not implemented"
