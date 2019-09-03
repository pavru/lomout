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

package net.pototskiy.apps.lomout.jcommander

import com.beust.jcommander.DynamicParameter
import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters

/**
 * CLI main command to process config and data files
 *
 * @property configFile String
 * @property logLevel String
 * @property sqlLogLevel String
 * @property scriptCacheDir String
 * @property doNotUseScriptCache Boolean
 */
@Parameters(
    commandNames = ["--process"],
    commandDescription = "Process files"
)
class CommandMain {
    @Parameter(
        description = "configuration file",
        required = true
    )
    var configFile: MutableList<String> = mutableListOf()
    @Parameter(
        names = ["-l", "--log-level"],
        description = "log level: fatal, error, warn, info, trace",
        arity = 1
    )
    var logLevel: String = "warn"
    @Parameter(
        names = ["-s", "--sql-log-level"],
        description = "log level: fatal, error, warn, info, trace",
        arity = 1
    )
    var sqlLogLevel: String = "error"
    @Parameter(
        names = ["--script-cache-dir", "-d"],
        description = "Directory where cached script store",
        arity = 1
    )
    var scriptCacheDir: String = "tmp/script/cache"
    @Parameter(
        names = ["--do-not-use-cache", "-n"],
        description = "Do not use cached script",
        arity = 0
    )
    var doNotUseScriptCache: Boolean = false
    @DynamicParameter(
        names = ["-S"],
        description = "Script parameter in format name=value"
    )
    var scriptParameters: MutableMap<String, String> = mutableMapOf()
}
