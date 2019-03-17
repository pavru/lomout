package net.pototskiy.apps.magemediation.api.config.mediator

abstract class AbstractLine(
    val lineType: LineType,
    val inputEntities: InputEntityCollection,
    val pipeline: Pipeline
) {
    enum class LineType { CROSS, UNION }
}
