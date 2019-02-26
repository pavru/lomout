package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

abstract class AttributeWriterPlugin<T : Type> : Plugin() {
    abstract fun write(attribute: Attribute<T>, value: T?, cell: Cell)
}

typealias AttributeWriterFunction<T> = (Attribute<T>, T?, cell: Cell) -> Unit
