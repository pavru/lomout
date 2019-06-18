package net.pototskiy.apps.lomout.api.entity.helper

import net.pototskiy.apps.lomout.api.database.EntityBooleans
import net.pototskiy.apps.lomout.api.database.EntityDateTimes
import net.pototskiy.apps.lomout.api.database.EntityDates
import net.pototskiy.apps.lomout.api.database.EntityDoubles
import net.pototskiy.apps.lomout.api.database.EntityLongs
import net.pototskiy.apps.lomout.api.database.EntityStrings
import net.pototskiy.apps.lomout.api.database.EntityTexts
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Entity
import net.pototskiy.apps.lomout.api.entity.type.Type
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.full.primaryConstructor

private val attributeTables = listOf(
    EntityStrings,
    EntityTexts,
    EntityBooleans,
    EntityLongs,
    EntityDoubles,
    EntityDateTimes,
    EntityDates
)

internal fun loadEntityAttributes(entity: Entity): Map<AnyTypeAttribute, Type> {
    val values = entity.type.attributeTables.map { table ->
        transaction {
            table.select { table.owner eq entity.id }
                .asSequence()
                .map {
                    AttributeRow(
                        it[table.owner],
                        it[table.code],
                        it[table.index],
                        it[table.value]
                    )
                }.groupBy { it.code }
                .map { it.key to it.value }
                .toList()
        }
    }.flatten().toMap()
    return entity.type.attributes.mapNotNull { attr ->
        values[attr.name]?.let { rawData ->
            val value = rawData.map { it.index to it.value }.toMap()
            when {
                value.isEmpty() -> null
                value.size > 1 || !value.containsKey(-1) ->
                    attr.type.primaryConstructor?.call(value.values.toList())
                else -> value[-1] as Type
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
