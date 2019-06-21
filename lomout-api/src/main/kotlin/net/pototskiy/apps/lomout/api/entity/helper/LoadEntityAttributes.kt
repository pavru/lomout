package net.pototskiy.apps.lomout.api.entity.helper

import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Entity
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.entity.type.isList
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.full.primaryConstructor

internal fun loadEntityAttributes(entity: Entity): Map<AnyTypeAttribute, Type> {
    val values = entity.type.attributeTables.map { table ->
        transaction {
            table
                .slice(table.code, table.value)
                .select { table.owner eq entity.id }
                .map { AttributeRow(it[table.code], it[table.value]) }
        }
    }.flatten().groupBy({ it.code }, { it.value })

    return entity.type.attributes.mapNotNull { attr ->
        values[attr.name]?.let { rawData ->
            val isList = attr.type.isList()
            when {
                rawData.isEmpty() -> null
                isList -> attr.type.primaryConstructor?.call(rawData)
                else -> rawData[0]
            }
        }?.let { attr to it }
    }.toMap()
}

private data class AttributeRow(
    val code: String,
    val value: Type
)
