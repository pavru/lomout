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

package net.pototskiy.apps.lomout.api.source.workbook

import java.io.Closeable

/**
 * Source data workbook interface
 *
 * @property name String
 * @property type WorkbookType
 */
interface Workbook : Iterable<Sheet>, Closeable {
    /**
     * Workbook name
     */
    val name: String
    /**
     * Workbook type
     */
    val type: WorkbookType

    /**
     * Get sheet by name
     *
     * @param sheet String The sheet name
     * @return Sheet
     */
    operator fun get(sheet: String): Sheet

    /**
     * Get sheet by the index
     *
     * @param sheet Int The sheet index, zero based
     * @return Sheet
     */
    operator fun get(sheet: Int): Sheet

    /**
     * Insert sheet into the workbook
     *
     * @param sheet String The sheet name to insert
     * @return Sheet The inserted sheet
     */
    fun insertSheet(sheet: String): Sheet

    /**
     * Test if workbook has a sheet with given name
     *
     * @param sheet String The sheet name
     * @return Boolean
     */
    fun hasSheet(sheet: String): Boolean
}
