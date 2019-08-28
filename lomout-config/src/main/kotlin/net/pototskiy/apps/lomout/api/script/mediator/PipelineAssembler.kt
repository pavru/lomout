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

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.entity.EntityCollection
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract pipeline assembler
 */
sealed class PipelineAssembler<T : Document> {
    /**
     * Assembler function
     *
     * @param entities PipelineDataCollection The pipeline input entities
     * @return Map<AnyTypeAttribute, Type?> The target entity attributes
     */
    @Suppress("UNCHECKED_CAST")
    operator fun invoke(entities: EntityCollection): Document? = when (this) {
        is PipelineAssemblerWithPlugin<*> -> (pluginClass as KClass<PipelineAssemblerPlugin<T>>)
            .createInstance().let {
                it.apply(options as (PipelineAssemblerPlugin<T>.() -> Unit))
                it.assemble(entities)
            }
        is PipelineAssemblerWithFunction<*> -> PluginContext.function(entities)
    }
}

/**
 * Pipeline assembler with a plugin
 *
 * @property pluginClass KClass<out PipelineAssemblerPlugin> The assembler plugin class
 * @property options The plugin options
 * @constructor
 */
class PipelineAssemblerWithPlugin<T : Document>(
    val pluginClass: KClass<PipelineAssemblerPlugin<T>>,
    val options: PipelineAssemblerPlugin<T>.() -> Unit = {}
) : PipelineAssembler<T>()

/**
 * Pipeline assembler with inline function
 *
 * @property function [PipelineAssemblerFunction] The assembler function
 * @constructor
 */
class PipelineAssemblerWithFunction<T : Document>(
    val function: PipelineAssemblerFunction<T>
) : PipelineAssembler<T>()
