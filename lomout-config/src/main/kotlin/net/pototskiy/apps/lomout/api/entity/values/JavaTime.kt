/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.pototskiy.apps.lomout.api.entity.values

import java.time.Duration
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

/**
 * Get duration in secondes as Double
 */
@Suppress("MagicNumber")
val Duration.secondFractions: Double
    get() {
        return this.nano.toDouble() / 1000000000.0
    }
