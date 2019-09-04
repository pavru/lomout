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

package net.pototskiy.apps.lomout.api.script.mediator

import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.PipelineAssemblerFunction
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.entity.EntityCollection
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import net.pototskiy.apps.lomout.api.callable.PipelineAssembler as PipelineAssemblerCallable

/**
 * Abstract pipeline assembler
 */
sealed class Assembler<T : Document> {
    /**
     * Assembler function
     *
     * @param entities PipelineDataCollection The pipeline input entities
     * @return Map<AnyTypeAttribute, Type?> The target entity attributes
     */
    @Suppress("UNCHECKED_CAST")
    operator fun invoke(context: LomoutContext, entities: EntityCollection): Document? = when (this@Assembler) {
        is AssemblerWithCallable<*> -> (pluginClass as KClass<PipelineAssemblerCallable<T>>)
            .createInstance().let {
                it.apply(options as (PipelineAssemblerCallable<T>.() -> Unit))
                it(entities)
            }
        is AssemblerWithFunction<*> -> function(context, entities)
    }
}

/**
 * Pipeline assembler with a plugin
 *
 * @property pluginClass KClass<out PipelineAssemblerPlugin> The assembler plugin class
 * @property options The plugin options
 * @constructor
 */
class AssemblerWithCallable<T : Document>(
    val pluginClass: KClass<PipelineAssemblerCallable<T>>,
    val options: PipelineAssemblerCallable<T>.() -> Unit = {}
) : Assembler<T>()

/**
 * Pipeline assembler with inline function
 *
 * @property function [PipelineAssemblerFunction] The assembler function
 * @constructor
 */
class AssemblerWithFunction<T : Document>(
    val function: PipelineAssemblerFunction<T>
) : Assembler<T>()
