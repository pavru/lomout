package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.Generated
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.entity.EntityStatus
import net.pototskiy.apps.lomout.api.entity.EntityStatus.CREATED
import net.pototskiy.apps.lomout.api.entity.EntityStatus.REMOVED
import net.pototskiy.apps.lomout.api.entity.EntityStatus.UNCHANGED
import net.pototskiy.apps.lomout.api.entity.EntityStatus.UPDATED
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.type.Type

/**
 * Pipeline input entity
 *
 * @property entity EntityType The entity type
 * @property statuses The allowed statuses for input entity
 * @property extAttributes The extension attributes
 * @constructor
 */
data class InputEntity(
    val entity: EntityType,
    val statuses: Array<EntityStatus>,
    val extAttributes: AttributeCollection
) {

    /**
     * Pipeline input entity definition builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property entityType EntityType The base entity type
     * @property statuses Allowed statuses for input entity
     * @property extAttributes The extension attributes
     * @constructor
     */
    @ConfigDsl
    class Builder(
        val helper: ConfigBuildHelper,
        val entityType: EntityType
    ) {
        private var statuses: Array<EntityStatus> = arrayOf(CREATED, UPDATED, UNCHANGED, REMOVED)
        val extAttributes = mutableListOf<AnyTypeAttribute>()

        /**
         * Entity status to include to processing. By default, all statuses are allowed.
         *
         * @param status Array<out EntityStatus>
         */
        @ConfigDsl
        fun statuses(vararg status: EntityStatus) {
            @Suppress("UNCHECKED_CAST")
            this.statuses = status as Array<EntityStatus>
        }

        /**
         * Define an extended attribute of input entity. Extension attribute **must have** builder.
         *
         * ```
         * ...
         *  extAttribute<Type>("ext name", "base name") {
         *      builder {...}
         *      reader {...}
         *      writer {...}
         *  }
         * ...
         * ```
         *
         * @param T The extended attribute type
         * @param name The name of extended attribute
         * @param block The extended attribute definition
         */
        @ConfigDsl
        inline fun <reified T : Type> extAttribute(
            name: String,
            block: Attribute.Builder<T>.() -> Unit = {}
        ) {
            val extAttr = Attribute.Builder(helper, name, T::class).apply(block).build()
            if (!extAttr.isSynthetic) {
                throw AppConfigException(badPlace(extAttr), "Extension attribute must have builder")
            }
            this.extAttributes.add(extAttr)
        }

        /**
         * Build input entity definition
         *
         * @return InputEntity
         */
        fun build(): InputEntity {
            return InputEntity(
                entityType,
                statuses,
                AttributeCollection(extAttributes)
            )
        }
    }

    /**
     * Test equal
     *
     * @param other The other
     * @return The result
     */
    @Generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InputEntity) return false

        if (entity != other.entity) return false
        if (!statuses.contentEquals(other.statuses)) return false
        if (extAttributes != other.extAttributes) return false

        return true
    }

    /**
     * Generate hash code
     *
     * @return The hash code
     */
    @Generated
    override fun hashCode(): Int {
        var result = entity.hashCode()
        result = 31 * result + statuses.contentHashCode()
        result = 31 * result + extAttributes.hashCode()
        return result
    }
}
