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

package net.pototskiy.apps.lomout.api.document

import net.pototskiy.apps.lomout.api.document.SupportAttributeType.booleanListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.booleanType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateTimeType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.datetimeListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.documentType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.doubleListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.doubleType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.floatListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.floatType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initBooleanListValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initBooleanValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDateListValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDateTimeListValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDateTimeValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDateValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDocumentValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDoubleListValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDoubleValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initFloatListValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initFloatValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initIntListValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initIntValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initLongListValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initLongValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initStringListValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initStringValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.intListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.intType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.longListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.longType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.stringListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.stringType
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Attribute supported types
 *
 * @property booleanType The boolean type
 * @property initBooleanValue The boolean init value
 * @property booleanListType The boolean list type
 * @property initBooleanListValue The boolean list init value
 * @property intType The int type
 * @property initIntValue The int init value
 * @property intListType The int list type
 * @property initIntListValue The int list init value
 * @property longType The long type
 * @property initLongValue The long init value
 * @property longListType The long list type
 * @property initLongListValue The long list init value
 * @property floatType The float type
 * @property initFloatValue The float init value
 * @property floatListType The float list type
 * @property initFloatListValue The float list init value
 * @property doubleType The double type
 * @property initDoubleValue The double init value
 * @property doubleListType The double list type
 * @property initDoubleListValue The double list init value
 * @property stringType The string type
 * @property initStringValue The string init value
 * @property stringListType The string list type
 * @property initStringListValue The string list init value
 * @property dateTimeType The datetime type
 * @property initDateTimeValue The datetime init value
 * @property datetimeListType The datetime list type
 * @property initDateTimeListValue The datetime list init value
 * @property dateType The date type
 * @property initDateValue The date init value
 * @property dateListType The date list type
 * @property initDateListValue The date list init value
 * @property documentType The document type
 * @property initDocumentValue The document init value
 */
@Suppress("ObjectPropertyName", "unused")
object SupportAttributeType {
    private val _booleanType: Boolean? = true
    private val _intType: Int? = null
    private val _longType: Long? = null
    private val _floatType: Float? = null
    private val _doubleType: Double? = null
    private val _stringType: String? = null
    private val _datetimeType: LocalDateTime? = null
    private val _dateType: LocalDate? = null
    private val _documentType: Document? = null
    private val _booleanListType: List<Boolean>? = null
    private val _intListType: List<Int>? = null
    private val _longListType: List<Long>? = null
    private val _floatListType: List<Float>? = null
    private val _doubleListType: List<Double>? = null
    private val _stringListType: List<String>? = null
    private val _datetimeListType: List<LocalDateTime>? = null
    private val _dateListType: List<LocalDate>? = null

    val booleanType = ::_booleanType.returnType
    const val initBooleanValue = true
    val intType = ::_intType.returnType
    const val initIntValue = 0
    val longType = ::_longType.returnType
    const val initLongValue = 0L
    val floatType = ::_floatType.returnType
    const val initFloatValue = 0.0f
    val doubleType = ::_doubleType.returnType
    const val initDoubleValue = 0.0
    val stringType = ::_stringType.returnType
    const val initStringValue = ""
    val dateTimeType = ::_datetimeType.returnType
    val initDateValue: LocalDate = LocalDate.now()
    val dateType = ::_dateType.returnType
    val initDateTimeValue: LocalDateTime = LocalDateTime.now()
    val documentType = ::_documentType.returnType
    val initDocumentValue = InitDocument()
    val booleanListType = ::_booleanListType.returnType
    val initBooleanListValue = emptyList<Boolean>()
    val intListType = ::_intListType.returnType
    val initIntListValue = emptyList<Int>()
    val longListType = ::_longListType.returnType
    val initLongListValue = emptyList<Long>()
    val floatListType = ::_floatListType.returnType
    val initFloatListValue = emptyList<Float>()
    val doubleListType = ::_doubleListType.returnType
    val initDoubleListValue = emptyList<Double>()
    val stringListType = ::_stringListType.returnType
    val initStringListValue = emptyList<String>()
    val datetimeListType = ::_datetimeListType.returnType
    val initDateTimeListValue = emptyList<LocalDateTime>()
    val dateListType = ::_dateListType.returnType
    val initDateListValue = emptyList<LocalDate>()

    /**
     * Attribute types
     */
    val types = listOf(
        ::_booleanType.returnType,
        ::_intType.returnType,
        ::_longType.returnType,
        ::_floatType.returnType,
        ::_doubleType.returnType,
        ::_stringType.returnType,
        ::_datetimeType.returnType,
        ::_dateType.returnType,
        ::_booleanListType.returnType,
        ::_intListType.returnType,
        ::_longListType.returnType,
        ::_floatListType.returnType,
        ::_doubleListType.returnType,
        ::_stringListType.returnType,
        ::_datetimeListType.returnType,
        ::_dateListType.returnType,
        ::_documentType.returnType
    )
    /**
     * Attribute list types
     */
    val listTypes = listOf(
        ::_booleanListType.returnType,
        ::_intListType.returnType,
        ::_longListType.returnType,
        ::_floatListType.returnType,
        ::_doubleListType.returnType,
        ::_stringListType.returnType,
        ::_datetimeListType.returnType,
        ::_dateListType.returnType
    )

    /**
     * The document type init value
     */
    class InitDocument : Document() {
        /**
         * Document metadata
         */
        companion object : DocumentMetadata(InitDocument::class)
    }
}
