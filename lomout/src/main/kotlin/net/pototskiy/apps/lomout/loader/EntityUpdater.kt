package net.pototskiy.apps.lomout.loader

import net.pototskiy.apps.lomout.api.document.Document
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import kotlin.reflect.KClass

class EntityUpdater(
    private val repository: EntityRepositoryInterface,
    private val entityType: KClass<out Document>
) {

    fun update(data: Map<Attribute, Any>): Long {
        val processedRows: Long
        var entity = repository.get(
            entityType,
            data.filter { it.key.isKey },
            includeDeleted = true
        )
        if (entity == null) {
            entity = repository.create(entityType)
            data.forEach { entity.setAttribute(it.key.name, it.value) }
            repository.update(entity)
            processedRows = 1L
        } else {
            entity.touch()
            processedRows = testAndUpdateTypedAttributes(entity, data)
            if (processedRows != 0L) {
                entity.markUpdated()
                repository.update(entity)
            } else {
                repository.updateCommonPart(entity)
            }
        }
        return processedRows
    }

    private fun testAndUpdateTypedAttributes(entity: Document, data: Map<Attribute, Any>): Long {
        var updatedRows = 0L
        entity.documentMetadata.attributes.values.forEach {
            if (data.containsKey(it) && entity.getAttribute(it.name) != data[it]) {
                entity.setAttribute(it.name, data[it])
                updatedRows = 1L
            }
        }
        return updatedRows
    }
}
