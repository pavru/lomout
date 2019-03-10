package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

abstract class AttributeWriterPlugin<T : Type> : Plugin() {
    abstract fun write(value: T?, cell: Cell)
}

typealias AttributeWriterFunction<T> = (T?, cell: Cell) -> Unit
