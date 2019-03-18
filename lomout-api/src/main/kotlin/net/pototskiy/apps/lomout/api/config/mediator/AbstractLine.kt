package net.pototskiy.apps.lomout.api.config.mediator

abstract class AbstractLine(
    val lineType: LineType,
    val inputEntities: InputEntityCollection,
    val pipeline: Pipeline
) {
    enum class LineType { CROSS, UNION }
}
