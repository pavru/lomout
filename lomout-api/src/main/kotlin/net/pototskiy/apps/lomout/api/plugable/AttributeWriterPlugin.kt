package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.source.workbook.Cell

abstract class AttributeWriterPlugin<T : Type> : Plugin() {
    abstract fun write(value: T?, cell: Cell)
}

typealias AttributeWriterFunction<T> = (T?, cell: Cell) -> Unit
