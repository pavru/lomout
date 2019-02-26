package net.pototskiy.apps.magemediation.api.plugable

import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.api.source.workbook.Cell

abstract class AttributeReaderPlugin<T: Type> : Plugin() {
    abstract fun read(attribute: Attribute<out T>, input: Cell): T?
}

typealias AttributeReaderFunction<T> = (Attribute<out T>, Cell) -> T?
