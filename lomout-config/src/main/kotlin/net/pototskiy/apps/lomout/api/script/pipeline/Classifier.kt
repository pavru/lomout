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

package net.pototskiy.apps.lomout.api.script.pipeline

import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.PipelineClassifierFunction
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import net.pototskiy.apps.lomout.api.callable.PipelineClassifier as PipelineClassifierCallable

/**
 * Abstract pipeline classifier
 */
sealed class Classifier {
    /**
     * Classifier function
     *
     * @param element ClassifierElement Element to classify
     * @return ClassifierElement
     */
    operator fun invoke(context: LomoutContext, element: ClassifierElement): ClassifierElement {
        return when (this@Classifier) {
            is ClassifierWithPlugin -> pluginClass.createInstance().let {
                it.apply(options)
                it(element)
            }
            is ClassifierWithFunction -> function(context, element)
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
class ClassifierWithPlugin(
    val pluginClass: KClass<out PipelineClassifierCallable>,
    val options: PipelineClassifierCallable.() -> Unit = {}
) : Classifier()

/**
 * Pipeline classifier with function
 *
 * @property function The classifier function
 * @constructor
 */
class ClassifierWithFunction(val function: PipelineClassifierFunction) : Classifier()
