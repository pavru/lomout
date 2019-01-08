package net.pototskiy.apps.magemediation

import org.joda.time.DateTime
import java.util.*

const val LOG_NAME = "Import"
const val UNDEFINED_COLUMN = -1
val DEFAULT_LOCALE: String = "${Locale.getDefault().isO3Language}_${Locale.getDefault().isO3Country}"
val TIMESTAMP = DateTime()