package importer

import kotlin.contracts.ExperimentalContracts

class Importer {
    @ExperimentalContracts
    fun prepareImportFiles() {
        OneGroup.addUpdate()
        if (!OneProduct.addUpdate()) return
    }
}