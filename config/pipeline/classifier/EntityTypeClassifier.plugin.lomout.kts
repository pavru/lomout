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

import net.pototskiy.apps.lomout.api.LomoutContext
import net.pototskiy.apps.lomout.api.callable.PipelineClassifier
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.script.pipeline.ClassifierElement
import kotlin.reflect.KClass

class EntityTypeClassifier : PipelineClassifier() {
    var typeList: List<KClass<out Document>> = emptyList()

    override operator fun invoke(element: ClassifierElement, context: LomoutContext): ClassifierElement {
        return if (element.entities.any { it::class in typeList }) {
            element.match()
        } else {
            element.mismatch()
        }
    }
}
