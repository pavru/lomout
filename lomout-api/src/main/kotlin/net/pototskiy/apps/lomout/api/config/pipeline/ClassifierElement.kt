package net.pototskiy.apps.lomout.api.config.pipeline

import net.pototskiy.apps.lomout.api.entity.Entity
import net.pototskiy.apps.lomout.api.entity.EntityCollection

/**
 * Pipeline classifier result
 */
sealed class ClassifierElement {
    /**
     * Entity IDs of element to classify
     */
    abstract val entities: EntityCollection

    /**
     * Element that is matched to classifier
     *
     * @property entities The entity collection
     * @constructor
     */
    class Matched(override val entities: EntityCollection) : ClassifierElement()

    /**
     * Element that is not matched to classifier
     *
     * @property entities The entity collection
     * @constructor
     */
    class Mismatched(override val entities: EntityCollection) : ClassifierElement()

    /**
     * Element should be skipped
     *
     * @property entities Entities list
     * @constructor
     */
    class Skipped(override val entities: EntityCollection) : ClassifierElement()

    /**
     * Create the matched element
     *
     * @receiver ClassifierElement
     * @return Matched
     */
    fun match() = Matched(this.entities)

    /**
     * Create the matched element
     *
     * @param entity The entity to add to element
     * @return Matched
     */
    fun match(entity: Entity) = Matched(EntityCollection(this.entities.plus(entity)))

    /**
     * Create the matched element
     *
     * @param entities Entities to add to element
     * @return Matched
     */
    fun match(entities: EntityCollection) = Matched(EntityCollection(this.entities.plus(entities)))

    /**
     * Create the matched element
     *
     * @param entities Entities to add to element
     * @return Matched
     */
    fun match(entities: List<Entity>) = Matched(EntityCollection(this.entities.plus(entities)))

    /**
     * Create the element mismatched
     *
     * @receiver ClassifierElement
     * @return Mismatched
     */
    fun mismatch() = Mismatched(this.entities)

    /**
     * Create the skipped element
     *
     * @return Skipped
     */
    fun skip() = Skipped(this.entities)
}
