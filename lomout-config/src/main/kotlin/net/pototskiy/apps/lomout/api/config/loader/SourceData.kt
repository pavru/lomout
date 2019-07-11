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

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.config.loader.SourceSheetDefinition.SourceSheetDefinitionWithName
import net.pototskiy.apps.lomout.api.config.loader.SourceSheetDefinition.SourceSheetDefinitionWithPattern
import net.pototskiy.apps.lomout.api.suspectedLocation

/**
 * Source data configuration
 *
 * @property file SourceFileDefinition The source data file
 * @property sheet SourceSheetDefinition The source data sheet of file
 * @property emptyRowBehavior EmptyRowBehavior The source data empty row behavior
 * @constructor
 */
data class SourceData(
    val file: SourceFileDefinition,
    val sheet: SourceSheetDefinition,
    val emptyRowBehavior: EmptyRowBehavior
) {
    /**
     * Source data builder class
     *
     * @property helper ConfigBuildHelper
     * @property file SourceFileDefinition?
     * @property sheet SourceSheetDefinition
     * @property emptyRowBehavior EmptyRowBehavior
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var file: SourceFileDefinition? = null
        private var sheet: SourceSheetDefinition = SourceSheetDefinitionWithPattern(Regex(".*"))
        private var emptyRowBehavior: EmptyRowBehavior = EmptyRowBehavior.IGNORE
        /**
         * Define the file of source data as reference to files block
         *
         * @param id String The file id
         */
        @Suppress("unused")
        fun file(id: String) {
            file = helper.definedSourceFiles.findRegistered(id)
                ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.config.load.source.file_not_defined", id)
                )
        }

        /**
         * Define source data sheet name
         *
         * @param sheet String The sheet name, for CSV file it must be **default**
         */
        @Suppress("unused")
        fun sheet(sheet: String) {
            this.sheet = SourceSheetDefinitionWithName(sheet)
        }

        /**
         * Define source data sheet pattern. All sheets fit to pattern will be used.
         *
         * @param sheet Regex
         */
        @Suppress("unused")
        fun sheet(sheet: Regex) {
            this.sheet = SourceSheetDefinitionWithPattern(sheet)
        }

        /**
         * Define stop behavior on empty row
         */
        @Suppress("unused")
        fun stopOnEmptyRow() {
            emptyRowBehavior = EmptyRowBehavior.STOP
        }

        /**
         * Define ignore behavior on empty row
         */
        @Suppress("unused")
        fun ignoreEmptyRows() {
            emptyRowBehavior = EmptyRowBehavior.IGNORE
        }

        /**
         * Build source data configuration
         *
         * @return SourceData
         */
        fun build(): SourceData = SourceData(
            this.file ?: throw AppConfigException(
                suspectedLocation(),
                message("message.error.config.load.source.file_not_defined", "")
            ),
            this.sheet,
            this.emptyRowBehavior
        )
    }
}
