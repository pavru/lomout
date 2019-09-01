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

import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.document.attribute.Price
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.text.ParseException

@Suppress("MagicNumber")
@Execution(ExecutionMode.CONCURRENT)
internal class LM56StringToPriceKtTest{
    @Test
    internal fun stringToPriceTest() {
        assertThat("123.123".stringToPrice("en_US".createLocale(), false)).isEqualTo(Price(123.123))
        assertThat("123123.123".stringToPrice("en_US".createLocale(), false)).isEqualTo(Price(123123.123))
        assertThat("123,123.123".stringToPrice("en_US".createLocale(), true)).isEqualTo(Price(123123.123))
        assertThat("123,123".stringToPrice("ru_RU".createLocale(), false)).isEqualTo(Price(123.123))
        assertThat("123 123,123".stringToPrice("ru_RU".createLocale(), true)).isEqualTo(Price(123123.123))
        assertThat("123.123123".stringToPrice("en_US".createLocale(), false)).isEqualTo(Price(123.1231))
        assertThat("123.123163".stringToPrice("en_US".createLocale(), false)).isEqualTo(Price(123.1232))
        assertThatThrownBy {
            "123,123.123".stringToPrice("en_US".createLocale(), false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessageContaining("String contains extra characters.")
        assertThatThrownBy {
            "12abc".stringToPrice("en_US".createLocale(), false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessageContaining("String contains extra characters.")
    }

    @Test
    internal fun priceToStringTest() {
        assertThat(Price(123123.123).priceToString("en_US".createLocale(), false)).isEqualTo("123123.123")
        assertThat(Price(123123.123987).priceToString("en_US".createLocale(), false)).isEqualTo("123123.124")
        assertThat(Price(123123.123987).priceToString("en_US".createLocale(), true)).isEqualTo("123,123.124")
    }
}
