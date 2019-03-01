package net.pototskiy.apps.magemediation.api.config.mediator

data class ProductionLineCollection(private val lines: List<ProductionLine>) : List<ProductionLine> by lines
