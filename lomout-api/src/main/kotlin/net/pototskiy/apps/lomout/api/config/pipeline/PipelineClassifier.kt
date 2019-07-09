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

import net.pototskiy.apps.lomout.api.plugable.PipelineClassifierFunction
import net.pototskiy.apps.lomout.api.plugable.PipelineClassifierPlugin
import net.pototskiy.apps.lomout.api.plugable.PluginContext
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Abstract pipeline classifier
 */
sealed class PipelineClassifier {
    /**
     * Classifier function
     *
     * @param element ClassifierElement Element to classify
     * @return ClassifierElement
     */
    operator fun invoke(element: ClassifierElement): ClassifierElement {
        return when (this) {
            is PipelineClassifierWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it.classify(element)
            }
            is PipelineClassifierWithFunction -> PluginContext.function(element)
        }
    }
}

/**
 * Pipeline classifier with a plugin
 *
 * @property pluginClass The classifier plugin class
 * @property options The classifier options
 * @constructor
 */
class PipelineClassifierWithPlugin(
    val pluginClass: KClass<out PipelineClassifierPlugin>,
    val options: PipelineClassifierPlugin.() -> Unit = {}
) : PipelineClassifier()

/**
 * Pipeline classifier with function
 *
 * @property function The classifier function
 * @constructor
 */
class PipelineClassifierWithFunction(val function: PipelineClassifierFunction) : PipelineClassifier()
