package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.document.DocumentMetadata

/**
 * Attributes collection
 *
 * @property attributes List<Attribute<out Type>>
 * @constructor
 */
data class AttributeCollection(private val attributes: List<DocumentMetadata.Attribute>) :
    List<DocumentMetadata.Attribute> by attributes {
    private val nameIndex = attributes.map { it.name to it }.toMap()
    /**
     * Find attribute by name.
     *
     * @param name String
     * @return DocumentMetadata.Attribute?
     */
    operator fun get(name: String): DocumentMetadata.Attribute? = nameIndex[name]
}
