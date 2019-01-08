package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.config.EmptyRowAction
import net.pototskiy.apps.magemediation.config.loader.dataset.DatasetConfiguration
import net.pototskiy.apps.magemediation.source.Sheet

interface LoaderInterface {
    fun load(sheet: Sheet, dataset: DatasetConfiguration, emptyRowAction: EmptyRowAction)
}