package net.pototskiy.apps.lomout.api.entity.helper

import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Entity
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.toEntity
import net.pototskiy.apps.lomout.api.entity.type.Type
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

internal fun findEntityByAttributes(
    type: EntityType,
    data: Map<AnyTypeAttribute, Type>,
    repository: EntityRepositoryInterface
): Entity? {
    var from: ColumnSet = DbEntityTable
    var where = Op.build { DbEntityTable.entityType eq type }
    data.forEach { (attr, value) ->
        value.table.let { table ->
            val alias = table.alias("${attr.name}_table")
            from = from.innerJoin(alias, { DbEntityTable.id }, { alias[table.owner] })
            where = where
                .and(Op.build { alias[table.code] eq attr.name })
                .and(Op.build { alias[table.value] eq value })
        }
    }
    return execFindQuery(from, where)?.toEntity(repository)
}

private fun execFindQuery(
    from: ColumnSet,
    where: Op<Boolean>
): ResultRow? {
    return transaction {
        from
            .slice(DbEntityTable.columns)
            .select { where }
            .limit(1)
            .toList()
    }.firstOrNull()
}
