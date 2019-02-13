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
val DEFAULT_LOCALE: String = "${Locale.getDefault().isO3Language}_${Locale.getDefault().isO3Country}"
val TIMESTAMP = DateTime()

const val ENTITY_TYPE_NAME_LENGTH = 100
