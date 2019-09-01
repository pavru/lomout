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

package net.pototskiy.apps.lomout.api.document.attribute

import org.bson.codecs.pojo.annotations.BsonCreator
import kotlin.math.roundToLong

/**
 * Type to represent price.
 */
@Suppress("MagicNumber")
class Price @BsonCreator constructor() {
    /**
     * Price value.
     */
    var value = 0.0

    /**
     * Create [Price] from [Double].
     *
     * @param price The value of price.
     */
    constructor(price: Double) : this() {
        value = (price * 10000.0).roundToLong() / 10000.0
    }

    /**
     * Get price double value.
     */
    fun toDouble(): Double = value

    /**
     * Equals
     *
     * @param other An other price.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Price

        if (value != other.value) return false

        return true
    }

    /**
     * Get price hash code.
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }

    /**
     * String presentation of [Price].
     */
    override fun toString(): String {
        return "Price(value=$value)"
    }
}
