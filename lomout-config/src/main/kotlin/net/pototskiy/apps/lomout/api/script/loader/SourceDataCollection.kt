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

import net.pototskiy.apps.lomout.api.script.ScriptBuildHelper
import net.pototskiy.apps.lomout.api.script.LomoutDsl

/**
 * Source data collection
 *
 * @property sourceData List<SourceData>
 * @constructor
 */
data class SourceDataCollection(private val sourceData: List<SourceData>) : List<SourceData> by sourceData {
    /**
     * Source data collection builder class
     *
     * @property helper The config build helper
     * @property sourceData MutableList<SourceData>
     * @constructor
     */
    @LomoutDsl
    class Builder(val helper: ScriptBuildHelper) {
        private val sourceData = mutableListOf<SourceData>()

        /**
         * Define sources collection
         *
         * ```
         * ...
         *  sources {
         *      source { file(..); sheet(...); }
         *      source { file(..); sheet(...); }
         *      ...
         *  }
         * ...
         * ```
         *
         * @receiver Builder
         * @param block The source definition
         * @return Boolean
         */
        @Suppress("unused")
        fun Builder.source(block: SourceData.Builder.() -> Unit) =
            sourceData.add(SourceData.Builder(helper).apply(block).build())

        /**
         * Build sources collection
         *
         * @return SourceDataCollection
         */
        fun build(): SourceDataCollection = SourceDataCollection(sourceData)
    }
}