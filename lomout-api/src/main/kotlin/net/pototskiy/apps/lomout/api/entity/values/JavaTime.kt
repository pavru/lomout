package net.pototskiy.apps.lomout.api.entity.values

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Convert legacy java Date to LocalDateTime at system timezone
 *
 * @receiver Date
 * @return (java.time.LocalDateTime..java.time.LocalDateTime?)
 */
fun Date.toLocalDateTime(): LocalDateTime = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

/**
 * Convert legacy java Date to LocalDate at system timezone
 *
 * @receiver Date
 * @return (java.time.LocalDateTime..java.time.LocalDateTime?)
 */
fun Date.toLocalDate(): LocalDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

/**
 * Convert [LocalDateTime] to legacy [Date] with system timezone
 *
 * @receiver LocalDateTime
 * @return (java.util.Date..java.util.Date?)
 */
fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())

/**
 * Convert [LocalDate] to legacy [Date] with system timezone
 *
 * @receiver LocalDate
 * @return (java.util.Date..java.util.Date?)
 */
fun LocalDate.toDate(): Date =
    Date.from(this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())

/**
 * Date to millis in system timezone
 */
val LocalDate.millis: Long
    get() = this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

/**
 * Datetime to millis in system timezone
 */
val LocalDateTime.millis: Long
    get() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
