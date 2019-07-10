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

package net.pototskiy.apps.lomout.api.log

import org.apache.logging.log4j.Level
import kotlin.script.experimental.api.ScriptDiagnostic

/**
 * Map config script compilation and evaluation message severity to log level
 */
private val severityToLevel = mapOf(
    ScriptDiagnostic.Severity.FATAL to Level.FATAL,
    ScriptDiagnostic.Severity.ERROR to Level.ERROR,
    ScriptDiagnostic.Severity.WARNING to Level.WARN,
    ScriptDiagnostic.Severity.INFO to Level.INFO,
    ScriptDiagnostic.Severity.DEBUG to Level.DEBUG
)

/**
 * Convert config script compilation and evaluation message severity to log level
 *
 * @receiver The message severity
 * @return The related log level
 */
fun ScriptDiagnostic.Severity.toLogLevel(): Level = severityToLevel.getValue(this)
