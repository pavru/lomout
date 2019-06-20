package net.pototskiy.apps.lomout.api.entity.helper

import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Entity
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.entity.type.isList
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.full.primaryConstructor

internal fun loadEntityAttributes(entity: Entity): Map<AnyTypeAttribute, Type> {
    val values = mutableMapOf<String, List<AttributeRow>>()
    entity.type.attributeTables.forEach { table ->
        values.putAll(transaction {
            table.select { table.owner eq entity.id }
                .asSequence()
                .map {
                    AttributeRow(
                        it[table.owner],
                        it[table.code],
                        it[table.index],
                        it[table.value]
                    )
                }
                .groupBy { it.code }
                .map { it.key to it.value }
                .toMap()
        }
        )
    }

    return entity.type.attributes.mapNotNull { attr ->
        values[attr.name]?.let { rawData ->
            val isList = attr.type.isList()
            val value = rawData.map { it.value }
            when {
                value.isEmpty() -> null
                isList -> attr.type.primaryConstructor?.call(value)
                else -> value[0]
            }
        }?.let { attr to it }
    }.toMap()
}

private data class AttributeRow(
    val owner: EntityID<Int>,
    val code: String,
    val index: Int,
    val value: Type
)
