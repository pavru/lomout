package net.pototskiy.apps.lomout.api.config.mediator

/**
 * Production line collection
 *
 * @property lines List<ProductionLine>
 * @constructor
 */
data class ProductionLineCollection(private val lines: List<ProductionLine>) : List<ProductionLine> by lines
