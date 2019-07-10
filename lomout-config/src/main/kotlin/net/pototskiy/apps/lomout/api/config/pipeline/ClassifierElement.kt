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

package net.pototskiy.apps.lomout.api.config.pipeline

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.entity.EntityCollection

/**
 * Pipeline classifier result
 */
sealed class ClassifierElement {
    /**
     * Entity IDs of element to classify
     */
    abstract val entities: EntityCollection

    /**
     * Element that is matched to classifier
     *
     * @property entities The entity collection
     * @constructor
     */
    class Matched(override val entities: EntityCollection) : ClassifierElement() {
        /**
         * Constructor for one entity
         *
         * @param entity The entity
         * @constructor
         */
        @Suppress("unused")
        constructor(entity: Document) : this(EntityCollection(listOf(entity)))
    }

    /**
     * Element that is not matched to classifier
     *
     * @property entities The entity collection
     * @constructor
     */
    class Mismatched(override val entities: EntityCollection) : ClassifierElement() {
        /**
         * Constructor for one entity
         *
         * @param entity The entity
         * @constructor
         */
        @Suppress("unused")
        constructor(entity: Document) : this(EntityCollection(listOf(entity)))
    }

    /**
     * Element should be skipped
     *
     * @property entities Entities list
     * @constructor
     */
    class Skipped(override val entities: EntityCollection) : ClassifierElement() {
        /**
         * Constructor for one entity
         *
         * @param entity The entity
         * @constructor
         */
        @Suppress("unused")
        constructor(entity: Document) : this(EntityCollection(listOf(entity)))
    }

    /**
     * Create the matched element
     *
     * @receiver ClassifierElement
     * @return Matched
     */
    fun match() = Matched(this.entities)

    /**
     * Create the matched element
     *
     * @param entity The entity to add to element
     * @return Matched
     */
    fun match(entity: Document) = Matched(EntityCollection(this.entities.plus(entity)))

    /**
     * Create the matched element
     *
     * @param entities Entities to add to element
     * @return Matched
     */
    fun match(entities: EntityCollection) = Matched(EntityCollection(this.entities.plus(entities)))

    /**
     * Create the matched element
     *
     * @param entities Entities to add to element
     * @return Matched
     */
    fun match(entities: List<Document>) = Matched(EntityCollection(this.entities.plus(entities)))

    /**
     * Create the element mismatched
     *
     * @receiver ClassifierElement
     * @return Mismatched
     */
    fun mismatch() = Mismatched(this.entities)

    /**
     * Create the skipped element
     *
     * @return Skipped
     */
    fun skip() = Skipped(this.entities)
}
