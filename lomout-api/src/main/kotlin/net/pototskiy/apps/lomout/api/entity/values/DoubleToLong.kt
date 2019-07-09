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

import net.pototskiy.apps.lomout.api.MessageBundle.message
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

/**
 * Double fraction as Double
 */
val Double.fraction: Double
    inline get() = (abs(this) - floor(abs(this))) * sign(this)

/**
 * Floor and cast to Long
 *
 * @receiver Double
 * @return Long
 */
fun Double.floorToLong(): Long = floor(this).toLong()

/**
 * Cast Double to Long
 *
 * @receiver Double
 * @return Long
 * @throws TypeCastException Double value fraction part is not zero
 */
fun Double.doubleToLong(): Long {
    return if (this.fraction == 0.0) {
        this.floorToLong()
    } else {
        throw TypeCastException(message("message.error.data.double.non_zero_fraction", this))
    }
}
