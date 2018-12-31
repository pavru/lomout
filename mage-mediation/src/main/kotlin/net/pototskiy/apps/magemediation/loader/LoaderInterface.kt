package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.config.dataset.Dataset
import net.pototskiy.apps.magemediation.config.dataset.EmptyRowAction
import net.pototskiy.apps.magemediation.source.Sheet

interface LoaderInterface {
    fun load(sheet: Sheet, dataset: Dataset, emptyRowAction: EmptyRowAction)
}