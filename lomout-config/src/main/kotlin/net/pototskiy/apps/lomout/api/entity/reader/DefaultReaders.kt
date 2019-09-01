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

package net.pototskiy.apps.lomout.api.entity.reader

import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.booleanListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.booleanType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateTimeType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.dateType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.datetimeListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.documentType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.doubleListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.doubleType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.intListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.intType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.longListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.longType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.stringListType
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.stringType

/**
 * Map of default readers
 */
val defaultReaders = mapOf(
    documentType to DocumentAttributeReader().apply {
        quotes = null
        delimiter = ','
        valueQuote = '"'
        valueDelimiter = '='
    },
    booleanType to BooleanAttributeReader().apply {
        locale = DEFAULT_LOCALE_STR
    },
    booleanListType to BooleanListAttributeReader().apply {
        locale = DEFAULT_LOCALE_STR
    },
    dateType to DateAttributeReader().apply {
        pattern = "d.M.uu"
    },
    dateListType to DateListAttributeReader().apply {
        pattern = "d.M.uu"
    },
    dateTimeType to DateTimeAttributeReader().apply {
        pattern = "d.M.uu H:m"
    },
    datetimeListType to DateTimeListAttributeReader().apply {
        pattern = "d.M.uu H:m"
    },
    doubleType to DoubleAttributeReader().apply {
        locale = DEFAULT_LOCALE_STR
    },
    doubleListType to DoubleListAttributeReader().apply {
        locale = DEFAULT_LOCALE_STR
    },
    longType to LongAttributeReader().apply {
        locale = DEFAULT_LOCALE_STR
    },
    longListType to LongListAttributeReader().apply {
        locale = DEFAULT_LOCALE_STR
    },
    intType to IntAttributeReader().apply {
        locale = DEFAULT_LOCALE_STR
    },
    intListType to IntListAttributeReader().apply {
        locale = DEFAULT_LOCALE_STR
    },
    stringType to StringAttributeReader(),
    stringListType to StringListAttributeReader().apply {
        quotes = '"'
        delimiter = ','
    }
)
