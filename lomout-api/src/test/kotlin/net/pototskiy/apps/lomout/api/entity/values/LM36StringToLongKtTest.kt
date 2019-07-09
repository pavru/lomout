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

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.text.ParseException
import java.util.*

@Execution(ExecutionMode.CONCURRENT)
internal class LM36StringToLongKtTest {

    @org.junit.jupiter.api.Test
    fun stringToLong() {
        assertThat("12".stringToLong(Locale("en_US"), false)).isEqualTo(12L)
        assertThat("12".stringToLong(Locale("ru_RU"), false)).isEqualTo(12L)
        assertThatThrownBy {
            "1.2".stringToLong(Locale("en_US"), false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String contains extra characters.")
        assertThatThrownBy {
            "1,2".stringToLong(Locale("en_US"), false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String contains extra characters.")
        assertThat("1,2".stringToLong(Locale("en_US"), true)).isEqualTo(12L)
        assertThat("1${160.toChar()}2".stringToLong(Locale("ru"), true)).isEqualTo(12L)
        assertThatThrownBy {
            "abc".stringToLong(Locale.US, true)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String cannot be parsed to long.")
    }
}
