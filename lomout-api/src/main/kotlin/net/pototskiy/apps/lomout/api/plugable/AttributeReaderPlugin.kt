package net.pototskiy.apps.lomout.api.plugable

import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.source.workbook.Cell

abstract class AttributeReaderPlugin<T : Type> : Plugin() {
    abstract fun read(attribute: Attribute<out T>, input: Cell): T?
}

typealias AttributeReaderFunction<T> = (Attribute<out T>, Cell) -> T?
