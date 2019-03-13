package net.pototskiy.apps.magemediation.api

import org.joda.time.DateTime
import java.util.*

const val ROOT_LOG_NAME = "net.pototskiy.apps.magemediation"
const val CONFIG_LOG_NAME = "net.pototskiy.apps.magemediation.config"
const val DATABASE_LOG_NAME = "net.pototskiy.apps.magemediation.database"
const val LOADER_LOG_NAME = "net.pototskiy.apps.magemediation.loader"
const val MEDIATOR_LOG_NAME = "net.pototskiy.apps.magemediation.mediator"
const val STATUS_LOG_NAME = "net.pototskiy.apps.magemediation.status"
const val EXPOSED_LOG_NAME = "Exposed"

const val UNDEFINED_COLUMN = -1
const val UNDEFINED_ROW = -1
val DEFAULT_LOCALE: Locale = Locale.getDefault()
val DEFAULT_LOCALE_STR: String = DEFAULT_LOCALE.toString()
val TIMESTAMP = DateTime()

const val ENTITY_TYPE_NAME_LENGTH = 100

// Standard strings
const val NOT_IMPLEMENTED = "not implemented"
