package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.config.excel.Dataset
import net.pototskiy.apps.magemediation.config.excel.EmptyRowAction
import net.pototskiy.apps.magemediation.source.Sheet

interface LoaderInterface {
    fun load(sheet: Sheet, dataset: Dataset, emptyRowAction: EmptyRowAction)
}