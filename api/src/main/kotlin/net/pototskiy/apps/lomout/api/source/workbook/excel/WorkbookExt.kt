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

package net.pototskiy.apps.lomout.api.source.workbook.excel

import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

private data class WorkbookFile(
    val workbook: WeakReference<Workbook>,
    val file: File
)

private val files = Collections.synchronizedList(mutableListOf<WorkbookFile>())

/**
 * Set workbook file name
 *
 * @receiver Workbook
 * @param file File
 */
@Synchronized
fun Workbook.setFileName(file: File) {
    cleanFiles()
    files.removeIf { it.workbook.get() == this }
    files.add(
        WorkbookFile(
            WeakReference(this),
            file
        )
    )
}

/**
 * Get file name of workbook
 *
 * @receiver Workbook
 * @return String
 */
fun Workbook.getFileName(): String = this.getFile()?.name ?: ""

/**
 * Get file of workbook
 *
 * @receiver Workbook
 * @return File?
 */
@Synchronized
fun Workbook.getFile(): File? {
    cleanFiles()
    return files.find { it.workbook.get() == this }?.file
}

private fun cleanFiles() {
    files.removeIf { it.workbook.get() == null }
}
