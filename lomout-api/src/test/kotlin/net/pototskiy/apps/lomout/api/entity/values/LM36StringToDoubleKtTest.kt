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
internal class LM36StringToDoubleKtTest {

    @org.junit.jupiter.api.Test
    fun stringToDouble() {
        assertThat("1.2".stringToDouble(Locale("en_US"), false)).isEqualTo(1.2)
        assertThatThrownBy {
            "1.2".stringToDouble(Locale("ru"), false)
        }.isInstanceOf(ParseException::class.java)
        assertThat("1,2".stringToDouble(Locale("ru"),false)).isEqualTo(1.2)
        assertThat("2${160.toChar()}1,2".stringToDouble(Locale("ru"),true)).isEqualTo(21.2)
        assertThatThrownBy {
            "2${160.toChar()}1,2".stringToDouble(Locale("ru"),false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String contains extra characters.")
        assertThatThrownBy {
            "abc".stringToDouble(Locale("ru"), false)
        }.isInstanceOf(ParseException::class.java)
            .hasMessage("String cannot be parsed to double.")
    }
}
