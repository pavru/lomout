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
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.script.LomoutDsl
import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.suspectedLocation

/**
 * Loader configuration class and builder
 *
 * @property files SourceFileCollection
 * @property loads LoadCollection
 * @constructor
 */
data class LoaderConfiguration(
    val files: SourceFileCollection,
    val loads: LoadCollection
) {
    /**
     * Loader configuration builder class
     *
     * @property helper ConfigBuildHelper
     * @property files SourceFileCollection?
     * @property loads MutableList<Load<*>>
     * @constructor
     */
    @LomoutDsl
    class Builder(val helper: ScriptBuildHelper) {
        private var files: SourceFileCollection? = null
        var loads = mutableListOf<Load<*>>()

        /**
         * Source file configuration
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
        fun files(block: SourceFileCollection.Builder.() -> Unit) {
            files = SourceFileCollection.Builder(helper).apply(block).build()
        }

        /**
         * Entity load entity configuration
         *
         * ```
         * ...
         *  load<"type name"> {
         *      fromSources {
         *          source { file(...); sheet(...); stopOnEmptyRow() }
         *          source { file(...); sheet(...); stopOnEmptyRow() }
         *          ...
         *      }
         *      rowsToSkip(number of rows)
         *      keepAbsentForDays(days)
         *      sourceFields {
         *          main("name") {...}
         *          extra("name") {...}
         *      }
         *  }
         * ...
         * ```
         * * fromSources — sources collection to load data from, **at least one source must be defined**
         * * rowsToSkip — number of rows (including header row) to skip before start loading, *optional*
         * * keepAbsentForDays — how long to keep entities that are absent in source data, entities will be
         *      marked as removed
         * * sourceFields — define source data fields
         *
         * @param block The entity loading instruction
         */
        inline fun <reified T : Document> load(block: Load.Builder<T>.() -> Unit) {
            loads.add(Load.Builder(helper, T::class).apply(block).build())
        }

        /**
         * Build loader configuration
         *
         * @return LoaderConfiguration
         */
        fun build(): LoaderConfiguration {
            val files =
                this.files ?: throw AppConfigException(
                    suspectedLocation(),
                    message("message.error.script.load.file.not_defined")
                )
            return LoaderConfiguration(
                files,
                LoadCollection(loads)
            )
        }
    }
}
