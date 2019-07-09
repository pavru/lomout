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

package net.pototskiy.apps.lomout.api

/**
 * Application related exception
 */
@Generated
open class AppException : Exception {
    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor() : super()

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?) : super(message)

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}

/**
 * All configuration problems
 */
@Generated
open class AppConfigException : AppException {
    /**
     * Exception place
     */
    val place: DomainExceptionPlace

    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace) : super() {
        this.place = place
    }

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace, message: String?) : super(message) {
        this.place = place
    }

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace, message: String?, cause: Throwable?) : super(message, cause) {
        this.place = place
    }
}

/**
 * Configuration problems related to entity type
 */
@Generated
open class AppDataException : AppException {
    /**
     * Exception place
     */
    val place: DomainExceptionPlace

    /**
     *
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace) : super() {
        this.place = place
    }

    /**
     *
     * @param message String?
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace, message: String?) : super(message) {
        this.place = place
    }

    /**
     *
     * @param message The message
     * @param cause The cause
     * @constructor
     */
    @Suppress("unused")
    constructor(place: DomainExceptionPlace, message: String?, cause: Throwable?) : super(message, cause) {
        this.place = place
    }
}
