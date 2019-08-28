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

package net.pototskiy.apps.lomout.api.script.loader

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.DEFAULT_LOCALE_STR
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.script.LomoutDsl
import net.pototskiy.apps.lomout.api.createLocale
import net.pototskiy.apps.lomout.api.suspectedLocation
import java.io.File
import java.util.*

/**
 * Source file collection
 *
 * @property files List<SourceFileDefinition>
 * @constructor
 */
data class SourceFileCollection(private val files: List<SourceFileDefinition>) : List<SourceFileDefinition> by files {
    /**
     * Source file collection builder
     *
     * @property helper ConfigBuildHelper
     * @property files MutableList<SourceFileDefinition>
     * @constructor
     */
    @LomoutDsl
    class Builder(private val helper: ScriptBuildHelper) {
        private val files = mutableListOf<SourceFileDefinition>()

        /**
         * File definition
         *
         * ```
         * ...
         *  file("file id") {
         *      path("file path")
         *      locale("ll_CC")
         *  }
         * ...
         * ```
         * * file — define the file with id
         * * [path][PathBuilder.path] — define file path, **mandatory**
         * * [locale][PathBuilder.locale] — define file locale, optional
         *
         * @see PathBuilder
         *
         * @param id String The file unique id
         * @param block The file definition
         */
        fun file(id: String, block: PathBuilder.() -> Unit) {
            val (file, locale) = PathBuilder().apply(block).build()
            val sourceFile = SourceFileDefinition(id, file, locale)
            files.add(sourceFile)
            helper.definedSourceFiles.register(sourceFile)
        }

        /**
         * Build source file collection
         *
         * @return SourceFileCollection
         */
        fun build() = SourceFileCollection(files)

        /**
         * File path builder
         *
         * @property path String?
         * @property locale String
         */
        class PathBuilder {
            private var path: String? = null
            private var locale: String = DEFAULT_LOCALE_STR

            /**
             * File path
             *
             * @param path String
             */
            fun path(path: String) {
                this.path = path
            }

            /**
             * File locale, default: *system locale*
             *
             * @param locale String
             */
            fun locale(locale: String) {
                this.locale = locale
            }

            /**
             * Path/locale build function
             *
             * @return Pair<File, Locale>
             */
            fun build(): Pair<File, Locale> {
                return Pair(
                    File(
                        path ?: throw AppConfigException(
                            suspectedLocation(),
                            message("message.error.script.load.file.path_not_defined")
                        )
                    ),
                    locale.createLocale()
                )
            }
        }
    }
}
