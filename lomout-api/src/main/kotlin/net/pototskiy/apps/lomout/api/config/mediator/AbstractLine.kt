package net.pototskiy.apps.lomout.api.config.mediator

/**
 * Base abstract line
 *
 * @property inputEntities InputEntityCollection The input entities collection
 * @property pipeline Pipeline The root pipeline
 * @constructor
 */
abstract class AbstractLine(
    val inputEntities: InputEntityCollection,
    val pipeline: Pipeline
)
