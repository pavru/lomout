package net.pototskiy.apps.lomout.api.config.pipeline

import net.pototskiy.apps.lomout.api.config.mediator.PipelineDataCollection
import net.pototskiy.apps.lomout.api.entity.EntityType
import org.jetbrains.exposed.dao.EntityID

/**
 * Pipeline classifier result
 */
sealed class ClassifierElement(
    private val pipelineDataCache: PipelineDataCache
) {
    /**
     * Entity IDs of element to classify
     */
    abstract val ids: List<ElementID>
    /**
     * Entities of element
     */
    val entities by lazy { PipelineDataCollection(ids.map { pipelineDataCache.readEntity(it.id) }) }

    /**
     * Classifier entity identifier
     *
     * @property type EntityType
     * @property id Int
     * @constructor
     */
    data class ElementID(
        val type: EntityType,
        val id: EntityID<Int>
    )

    /**
     * Element that is matched to classifier
     *
     * @property ids List<ElementID>
     * @constructor
     */
    class Matched(
        override val ids: List<ElementID>,
        pipelineDataCache: PipelineDataCache
    ) : ClassifierElement(pipelineDataCache)

    /**
     * Element that is not matched to classifier
     *
     * @property ids List<ElementID>
     * @constructor
     */
    class Mismatched(
        override val ids: List<ElementID>,
        pipelineDataCache: PipelineDataCache
    ) : ClassifierElement(pipelineDataCache)

    /**
     * Element should be skipped
     *
     * @property ids List<ElementID>
     * @constructor
     */
    class Skipped(
        override val ids: List<ElementID>,
        pipelineDataCache: PipelineDataCache
    ) : ClassifierElement(pipelineDataCache)

    /**
     * Create the matched element
     *
     * @receiver ClassifierElement
     * @return Matched
     */
    fun match() = Matched(this.ids, this.pipelineDataCache)

    /**
     * Create the matched element
     *
     * @param addElement ElementID
     * @return Matched
     */
    fun match(addElement: ElementID) =
        Matched(this.ids.plus(addElement), this.pipelineDataCache)

    /**
     * Create the matched element
     *
     * @param addElements List<ElementID>
     * @return Matched
     */
    fun match(addElements: List<ElementID>) =
        Matched(this.ids.plus(addElements), this.pipelineDataCache)

    /**
     * Create the element mismatched
     *
     * @receiver ClassifierElement
     * @return Mismatched
     */
    fun mismatch() = Mismatched(this.ids, this.pipelineDataCache)

    /**
     * Create the skipped element
     *
     * @return Skipped
     */
    fun skip() = Skipped(this.ids, this.pipelineDataCache)
}
