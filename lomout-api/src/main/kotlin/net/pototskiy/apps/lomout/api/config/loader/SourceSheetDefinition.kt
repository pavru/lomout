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

package net.pototskiy.apps.lomout.api.config.loader

/**
 * Source data sheet
 *
 * @property definition The sheet definition string presentation
 * @constructor
 */
sealed class SourceSheetDefinition {
    /**
     * Test if sheet name match with definition
     *
     * @param sheet The sheet name
     * @return Boolean
     */
    fun isMatch(sheet: String): Boolean {
        return when (this) {
            is SourceSheetDefinitionWithName -> sheet == this.name
            is SourceSheetDefinitionWithPattern -> this.pattern.matches(sheet)
        }
    }

    val definition: String
        get() = when (this) {
            is SourceSheetDefinitionWithName -> "name:$name"
            is SourceSheetDefinitionWithPattern -> "regex:$pattern"
        }

    /**
     * Source sheet definition with sheet name
     *
     * @property name The sheet name
     * @constructor
     */
    data class SourceSheetDefinitionWithName(val name: String) : SourceSheetDefinition()

    /**
     * Source sheet definition with regular expression pattern
     *
     * @property pattern The sheet name pattern
     * @constructor
     */
    data class SourceSheetDefinitionWithPattern(val pattern: Regex) : SourceSheetDefinition()
}
