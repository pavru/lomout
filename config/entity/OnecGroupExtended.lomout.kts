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

@file:Import("../reader/OnecGroupToLong.plugin.lomout.kts")

import OnecGroupToLong_plugin_lomout.OnecGroupToLong

class OnecGroupExtended : Document() {
    @Key
    @Reader(OnecGroupToLong::class)
    var group_code: Long = 0L
    var group_name: String? = null
    var magento_path: String? = null
    var url: String? = null
    var description: String? = null

    companion object : DocumentMetadata(OnecGroupExtended::class)
}
