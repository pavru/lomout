package net.pototskiy.apps.lomout.api.document

/**
 * Attribute indexes.
 *
 * @property indexes Indexes array
 * @constructor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Indexes(
    val indexes: Array<Index>
)

/**
 * Attribute index.
 *
 * @property name The index name
 * @property sortOrder The sort order
 * @property isUnique
 * @constructor
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Index(
    val name: String,
    val sortOrder: SortOrder = SortOrder.ASC,
    val isUnique: Boolean = false
) {
    /**
     * Sort order
     */
    enum class SortOrder {
        /**
         * Ascending sort
         *
         */
        ASC,
        /**
         * Descending sort
         *
         */
        DESC
    }
}
