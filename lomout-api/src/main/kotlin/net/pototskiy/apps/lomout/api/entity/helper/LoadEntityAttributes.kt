package net.pototskiy.apps.lomout.api.entity.helper

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Entity
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.BOOLEANLIST
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATELIST
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.type.DATETIMELIST
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.DOUBLELIST
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.LONGLIST
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.STRINGLIST
import net.pototskiy.apps.lomout.api.entity.type.TEXT
import net.pototskiy.apps.lomout.api.entity.type.TEXTLIST
import net.pototskiy.apps.lomout.api.entity.type.Type
import net.pototskiy.apps.lomout.api.entity.type.isSingle
import net.pototskiy.apps.lomout.api.plus
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.sql.ResultSet
import kotlin.reflect.full.primaryConstructor

internal fun loadEntityAttributes(entity: Entity): Map<AnyTypeAttribute, Type> {
    val sql = entity.type.attributeTables.joinToString(" union all ") { table ->
        "select `code`,`value` from ${table.tableName} where `owner` = ${entity.id.value}"
    }
    val newValues = mutableListOf<NewAttributeRow>()
    transaction {
        exec(sql) { rs ->
            while (rs.next()) {
                transform(entity.type, rs)?.let { newValues.add(it) }
            }
        }
    }
    return newValues
        .groupBy({ it.attribute }, { it.value })
        .mapValues { if (it.key.type.isSingle()) it.value[0] else it.key.type.primaryConstructor?.call(it.value)!! }
}

private fun transform(type: EntityType, resultSet: ResultSet): NewAttributeRow? {
    val attrName = resultSet.getString(1)
    val attr = type.getAttributeOrNull(attrName) ?: return null
    val value = when (attr.type) {
        BOOLEAN::class, BOOLEANLIST::class -> BOOLEAN(resultSet.getBoolean(2))
        STRING::class, STRINGLIST::class -> STRING(resultSet.getString(2))
        LONG::class, LONGLIST::class -> LONG(resultSet.getLong(2))
        DOUBLE::class, DOUBLELIST::class -> DOUBLE(resultSet.getDouble(2))
        DATE::class, DATELIST::class -> DATE(DateTime(resultSet.getDate(2)))
        DATETIME::class, DATETIMELIST::class -> DATETIME(DateTime(resultSet.getTimestamp(2)))
        TEXT::class, TEXTLIST::class -> TEXT(resultSet.getString(2))
        else -> throw AppDataException(
            badPlace(type) + attr,
            "Unexpected attribute type while reading data from the DB."
        )
    }
    return NewAttributeRow(attr, value)
}

private data class NewAttributeRow(
    val attribute: AnyTypeAttribute,
    val value: Type
)
