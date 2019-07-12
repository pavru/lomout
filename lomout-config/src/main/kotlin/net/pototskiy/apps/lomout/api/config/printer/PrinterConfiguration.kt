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

package net.pototskiy.apps.lomout.api.config.printer

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.config.loader.SourceFileCollection
import net.pototskiy.apps.lomout.api.suspectedLocation

/**
 * Printer part configuration
 *
 * @property files SourceFileCollection Files to print
 * @property lines PrinterLineCollection Printer lines
 * @constructor
 */
data class PrinterConfiguration(
    val files: SourceFileCollection,
    val lines: PrinterLineCollection
) {
    /**
     * Printer configuration builder class
     *
     * @property helper ConfigBuildHelper
     * @property files SourceFileCollection?
     * @property lines MutableList<PrinterLine>
     * @constructor
     */
    @ConfigDsl
    class Builder(private val helper: ConfigBuildHelper) {
        private var files: SourceFileCollection? = null
        private var lines = mutableListOf<PrinterLine>()

        /**
         * Define printer files like in the loader
         *
         * ```
         * ...
         *  files {
         *      file("file id") { path("file path"); locale("cc_LL") }
         *      file("file id") {
         *          path("file path")
         *          locale("cc_LL")
         *      }
         *      ...
         *  }
         * ...
         * ```
         * * [file][SourceFileCollection.Builder.file] — define file id, **mandatory**
         * * [path][SourceFileCollection.Builder.PathBuilder.path] — define file path, **mandatory**
         * * [locale][SourceFileCollection.Builder.PathBuilder.locale] — define file locale, optional
         *
         * @see SourceFileCollection
         *
         * @param block Files definition
         */
        @ConfigDsl
        fun files(block: SourceFileCollection.Builder.() -> Unit) {
            this.files = SourceFileCollection.Builder(helper).apply(block).build()
        }

        /**
         * Define printer line, **mandatory**
         *
         * ```
         * ...
         *  printerLine {
         *      input {...}
         *      output {...}
         *      pipeline {...}
         *  }
         * ...
         * ```
         * * [input][PrinterLine.Builder.input] — printer line input entities, **mandatory**
         * * [output][PrinterLine.Builder.output] — printer line output, **mandatory**
         * * [pipeline][PrinterLine.Builder.pipeline] — printer line processing pipeline, **mandatory**
         *
         * @param block The printer line definition
         */
        @ConfigDsl
        fun printerLine(block: PrinterLine.Builder.() -> Unit) {
            lines.add(PrinterLine.Builder(helper).apply(block).build())
        }

        /**
         * Build printer configuration
         *
         * @return PrinterConfiguration
         */
        fun build(): PrinterConfiguration {
            return PrinterConfiguration(
                files ?: throw AppConfigException(suspectedLocation(), message("message.error.config.print.no_files")),
                PrinterLineCollection(lines)
            )
        }
    }
}
