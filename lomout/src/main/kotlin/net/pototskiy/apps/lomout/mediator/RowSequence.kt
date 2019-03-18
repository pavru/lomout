package net.pototskiy.apps.lomout.mediator

import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

private const val PAGE_SIZE = 100

suspend fun <T> rowSequence(
    from: ColumnSet,
    where: Op<Boolean>,
    slice: List<Expression<*>>,
    block: (row: ResultRow) -> T
) = sequence {
    val count = transaction { from.select { where }.count() }
    for (page in 0..count / PAGE_SIZE) {
        transaction {
            from.slice(slice).select { where }.limit(PAGE_SIZE, page * PAGE_SIZE).toList()
        }.forEach { yield(block(it)) }
    }
}
