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
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.unknownPlace
import kotlin.reflect.KClass

/**
 * Field set configuration
 *
 * @property sets List<FieldSet> The list of field sets
 * @property mainSet FieldSet The main field set
 * @constructor
 */
data class FieldSetCollection(private val sets: List<FieldSet>) : List<FieldSet> by sets {
    val mainSet: FieldSet
        get() = this.find { it.mainSet }!!

    /**
     * Field sets builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property entityType EntityType The entity type
     * @property withSourceHeaders Boolean
     * @property sources SourceDataCollection? Sources to load data
     * @property headerRow Int? The header row number, zero based
     * @property fieldSets MutableList<FieldSet> List of fields
     * @constructor
     */
    @ConfigDsl
    class Builder(
        private val helper: ConfigBuildHelper,
        private val entityType: KClass<out Document>,
        private val withSourceHeaders: Boolean,
        private val sources: SourceDataCollection?,
        private val headerRow: Int?,
        private val toAttribute: Boolean
    ) {
        private val fieldSets = mutableListOf<FieldSet>()

        /**
         * Define main field set, **field set can have only one main set**
         *
         * ```
         * ...
         *  main("name") {
         *      field("name") {...} to attribute("name")
         *      field("name") {...} to attribute("name")
         *      field("name") {...}
         *      ...
         *  }
         * ...
         * ```
         *
         * @param name The field set name
         * @param block The main set definition
         * @return Boolean
         */
        @ConfigDsl
        fun main(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(
                FieldSet.Builder(
                    helper,
                    entityType,
                    name,
                    true,
                    withSourceHeaders,
                    sources,
                    headerRow,
                    toAttribute
                ).apply(block).build()
            )

        /**
         * Define extra field set, *several extra sets can be defined*
         *
         * ```
         * ...
         *  extra("name") {
         *      field("name") {...} to attribute("name")
         *      field("name") {...} to attribute("name")
         *      field("name") {...}
         *      ...
         *  }
         * ...
         * ```
         *
         * @param name The field set
         * @param block The extra field set definition
         * @return Boolean
         */
        @ConfigDsl
        fun extra(name: String, block: FieldSet.Builder.() -> Unit) =
            fieldSets.add(
                FieldSet.Builder(
                    helper,
                    entityType,
                    name,
                    false,
                    withSourceHeaders,
                    sources,
                    headerRow,
                    toAttribute
                ).apply(block).build()
            )

        /**
         * Build field sets collection
         *
         * @return FieldSetCollection
         */
        fun build(): FieldSetCollection {
            if (!fieldSets.any { it.mainSet }) {
                throw AppConfigException(unknownPlace(), message("message.error.config.fieldset.no_main_set"))
            }
            return FieldSetCollection(fieldSets)
        }
    }
}
