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

package net.pototskiy.apps.lomout.api.script

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.MessageBundle.message
import net.pototskiy.apps.lomout.api.script.loader.LoaderConfiguration
import net.pototskiy.apps.lomout.api.script.mediator.MediatorConfiguration
import net.pototskiy.apps.lomout.api.script.printer.PrinterConfiguration
import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.suspectedLocation
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

/**
 * Root element of configuration file
 *
 * @property database DatabaseConfig
 * @property loader LoaderConfiguration?
 * @property mediator MediatorConfiguration?
 * @property printer PrinterConfiguration?
 * @constructor
 */
data class LomoutScript(
    val database: DatabaseConfig,
    val loader: LoaderConfiguration?,
    val mediator: MediatorConfiguration?,
    val printer: PrinterConfiguration?
) {
    internal var scriptClassLoader = this::class.java.classLoader

    /**
     * Find entity type defined in the configuration. Only for test purpose.
     *
     * @param name String
     * @return KClass<out Document>?
     */
    @Suppress("TooGenericExceptionCaught")
    fun findEntityType(name: String): KClass<out Document>? {
        return try {
            val klass = scriptClassLoader.loadClass(name).kotlin
            if (klass.superclasses.contains(Document::class)) {
                @Suppress("UNCHECKED_CAST")
                klass as KClass<out Document>
            } else {
                null
            }
        } catch (e: Throwable) {
            null
        }
    }
    /**
     * Configuration root element builder class
     *
     * @property helper The configuration helper
     * @property database The database configuration
     * @property loader The loader configuration
     * @property mediator The mediator configuration
     * @property printer The printer configuration
     * @constructor
     */
    @LomoutDsl
    class Builder(private val helper: ScriptBuildHelper) {
        private var database: DatabaseConfig? = null
        private var loader: LoaderConfiguration? = null
        private var mediator: MediatorConfiguration? = null
        private var printer: PrinterConfiguration? = null

        /**
         * Database configuration
         *
         * ```
         * ...
         *  database {
         *      name("lomout")
         *      server {...}
         *  }
         * ...
         * ```
         * * name — MySql database name, **mandatory**
         * * [server][DatabaseConfig.Builder.server] — server configuration part, mandatory
         *
         * @see DatabaseConfig
         */
        @LomoutDsl
        fun database(block: DatabaseConfig.Builder.() -> Unit) {
            helper.pushScope("database")
            this.database = DatabaseConfig.Builder().apply(block).build()
            helper.popScope()
        }

        /**
         * Loader configuration
         *
         * ```
         * ...
         *  loader {
         *      files {...}
         *      load<"entity type name"> {...}
         *      load<"entity type name"> {...}
         *      ...
         *  }
         * ...
         * ```
         * * [files][LoaderConfiguration.Builder.files] — configure source files
         * * [load][LoaderConfiguration.Builder.load]
         *
         * @see LoaderConfiguration
         *
         * @param block The loader configuration
         */
        @LomoutDsl
        fun loader(block: LoaderConfiguration.Builder.() -> Unit) {
            helper.pushScope("loader")
            loader = LoaderConfiguration.Builder(helper).apply(block).build()
            helper.popScope()
        }

        /**
         * Mediator configuration
         *
         * @see MediatorConfiguration
         *
         * @param block The mediator configuration
         */
        @LomoutDsl
        fun mediator(block: MediatorConfiguration.Builder.() -> Unit) {
            helper.pushScope("mediator")
            mediator = MediatorConfiguration.Builder(helper).apply(block).build()
            helper.popScope()
        }

        /**
         * Printer configuration
         *
         * @see PrinterConfiguration
         *
         * @param block The printer configuration
         */
        @LomoutDsl
        fun printer(block: PrinterConfiguration.Builder.() -> Unit) {
            helper.pushScope("printer")
            this.printer = PrinterConfiguration.Builder(helper).also(block).build()
            helper.popScope()
        }

        /**
         * Build configuration
         *
         * @return Config
         */
        fun build(): LomoutScript {
            val realDatabase = database ?: DatabaseConfig.Builder().build()
            return LomoutScript(realDatabase, loader, mediator, printer)
        }
    }
}

/**
 * Root element of configuration
 *
 * ```
 * config {
 *      database {...}
 *      loader {...}
 *      mediator {...}
 *      printer {...}
 * }
 * ```
 * * [database][DatabaseConfig] — **mandatory**
 * * [loader][LoaderConfiguration] — optional
 * * [mediator][MediatorConfiguration] — optional
 * * [printer][PrinterConfiguration] — optional
 *
 * @see LomoutScript
 * @receiver Any
 * @param block The configuration
 */
fun Any.script(block: LomoutScript.Builder.() -> Unit) {
    val script = (this as? LomoutScriptTemplate)
    if (script != null) {
        val helper = ScriptBuildHelper()
        script.lomoutScript = LomoutScript.Builder(helper).apply(block).build()
    } else
        throw AppConfigException(suspectedLocation(), message("message.error.script.bad.object"))
}
