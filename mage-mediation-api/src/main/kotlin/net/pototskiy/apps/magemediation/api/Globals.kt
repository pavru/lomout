package net.pototskiy.apps.magemediation.api

import org.joda.time.DateTime
import java.util.*

const val LOADER_LOG_NAME = "Loader"
const val MEDIATOR_LOG_LEVEL = "Mediator"
const val STATUS_LOG_NAME = "Status"
const val EXPOSED_LOG_NAME = "Exposed"

const val UNDEFINED_COLUMN = -1
val DEFAULT_LOCALE: String = "${Locale.getDefault().isO3Language}_${Locale.getDefault().isO3Country}"
val TIMESTAMP = DateTime()