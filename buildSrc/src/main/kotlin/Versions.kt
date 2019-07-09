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

@Suppress("unused")
object Versions {
    const val kotlin = "1.3.41"
    const val dokka = "0.9.18"
    const val exposed = "0.15.1"
    val mysql = MysqlVersions(
        connector = "8.0.16"
    )
    const val ivy = "2.5.0-rc1"
    const val guava = "27.1-jre"
    const val jcommander = "1.71"
    const val junit5 = "5.5.0"
    const val assertj = "3.12.2"
    const val log4j = "2.12.0"
    const val slf4j = "1.8.0-beta4"
    const val progressBar = "0.7.2"
    const val jline = "3.9.0"
    const val poi = "4.1.0"
    const val commonCsv = "1.7"
    const val detekt = "1.0.0-RC16"
    const val cache2k = "1.2.2.Final"
    const val jcache = "1.1.1"
    const val hazelcast = "3.12"
    const val dbcp = "2.6.0"
    const val kmongo = "3.10.2"
    const val mongodb = "3.10.2"
}

data class MysqlVersions(
    val connector: String
)
