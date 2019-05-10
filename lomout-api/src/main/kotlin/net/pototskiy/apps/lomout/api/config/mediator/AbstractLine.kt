package net.pototskiy.apps.lomout.api.config.mediator

/**
 * Base abstract line
 *
 * @property lineType LineType The line type, how to generate input
 * @property inputEntities InputEntityCollection The input entities collection
 * @property pipeline Pipeline The root pipeline
 * @constructor
 */
abstract class AbstractLine(
    val lineType: LineType,
    val inputEntities: InputEntityCollection,
    val pipeline: Pipeline
) {
    /**
     * Line type
     */
    enum class LineType {
        /**
         * Cross line, generate cross join of input entities for root pipeline input
         */
        CROSS,
        /**
         * Union line, generate like SQL union of input entities for root pipeline input
         */
        UNION
    }
}
