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

package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata
import net.pototskiy.apps.lomout.api.entity.EntityCollection
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineAssemblerPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract pipeline assembler
 */
sealed class PipelineAssembler {
    /**
     * Assembler function
     *
     * @param target EntityType The target entity type
     * @param entities PipelineDataCollection The pipeline input entities
     * @return Map<AnyTypeAttribute, Type?> The target entity attributes
     */
    operator fun invoke(target: KClass<out Document>, entities: EntityCollection):
            Map<DocumentMetadata.Attribute, Any> {
        return when (this) {
            is PipelineAssemblerWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.assemble(target, entities)
            }
            is PipelineAssemblerWithFunction -> PluginContext.function(target, entities)
        }
    }
}

/**
 * Pipeline assembler with a plugin
 *
 * @property pluginClass KClass<out PipelineAssemblerPlugin> The assembler plugin class
 * @property options The plugin options
 * @constructor
 */
class PipelineAssemblerWithPlugin(
    val pluginClass: KClass<out PipelineAssemblerPlugin>,
    val options: PipelineAssemblerPlugin.() -> Unit = {}
) : PipelineAssembler()

/**
 * Pipeline assembler with inline function
 *
 * @property function [PipelineAssemblerFunction] The assembler function
 * @constructor
 */
class PipelineAssemblerWithFunction(
    val function: PipelineAssemblerFunction
) : PipelineAssembler()
