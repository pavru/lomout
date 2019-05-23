package net.pototskiy.apps.lomout.api.database

import net.pototskiy.apps.lomout.api.AppDatabaseException
import net.pototskiy.apps.lomout.api.TIMESTAMP
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.Type
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Exposed entity class for domain entity
 *
 * @property myTable DbEntityTable
 * @constructor
 * @param attributeClasses AttributeEntityClass<*, *> The attribute exposed entity classes
 */
@Suppress("SpreadOperator")
abstract class DbEntityClass(
    vararg attributeClasses: AttributeEntityClass<*, *>
) : DbEntityWithAttributeClass(*attributeClasses) {

    private val myTable by lazy { super.table as DbEntityTable }

    /**
     * Load entities from the DB
     *
     * @param entityType EntityType The entity type
     * @param withAttributes Boolean True load with attributes
     * @return List<DbEntity>
     */
    fun getEntities(entityType: EntityType, withAttributes: Boolean = false): List<DbEntity> =
        transaction { find { myTable.entityType eq entityType }.toList() }.also { list ->
            if (withAttributes) list.forEach { it.readAttributes() }
        }

    /**
     * Load entities from DB by attribute value
     *
     * @param entityType EntityType The entity type
     * @param attribute AnyTypeAttribute The attribute to search entity
     * @param value Type The attribute value
     * @return List<DbEntity>
     */
    fun getByAttribute(entityType: EntityType, attribute: AnyTypeAttribute, value: Type): List<DbEntity> =
        getEntitiesByAttributes(entityType, mapOf(attribute to value))

    /**
     * Load entities from DB by attributes value
     *
     * @param entityType EntityType The entity type
     * @param data Map<AnyTypeAttribute, Type?> The attribute-value pairs
     * @param onlyKeys Boolean True — use only key attributes
     * @return List<DbEntity>
     */
    fun getEntitiesByAttributes(
        entityType: EntityType,
        data: Map<AnyTypeAttribute, Type?>,
        onlyKeys: Boolean = false
    ): List<DbEntity> = transaction {
        var from: ColumnSet = myTable
        var where = Op.build { myTable.entityType eq entityType }
        val dataToUse = if (onlyKeys) data.filter { it.key.key } else data
        dataToUse.filterNot { it.value == null }.forEach { (attr, value) ->
            value as Type
            val attrClass = getAttributeClassFor(attr.valueType)
            val attrTable = attrClass.table as AttributeTable<*>
            val alias = attrTable.alias("${attr.name}_table")
            from = from.innerJoin(alias, { table.id }, { alias[attrTable.owner] })
            where = where.and(Op.build { alias[attrTable.code] eq attr.name })
            where = where.and(equalsBuild(alias[attrTable.value], value))
        }
        from
            .slice(table.columns)
            .select { where }
            .map { wrapRow(it) }
            .toList()
    }

    /**
     * Insert entity to DB
     *
     * @param entityType EntityType The entity type
     * @param data Map<AnyTypeAttribute, Type> The attribute values
     * @return DbEntity
     */
    fun insertEntity(entityType: EntityType, data: Map<AnyTypeAttribute, Type>): DbEntity {
        val entity = transaction {
            new {
                this.entityType = entityType
                touchedInLoading = true
                previousStatus = EntityStatus.CREATED
                currentStatus = EntityStatus.CREATED
                created = TIMESTAMP
                updated = TIMESTAMP
                absentDays = 0
            }
        }
        data.filterNot { it.key.isSynthetic || it.value.isTransient }
            .forEach { (attribute, value) -> addAttribute(entity, attribute, value) }
        return entity
    }

    /**
     * Reset change flag of entity
     *
     * @param entityType EntityType
     */
    fun resetTouchFlag(entityType: EntityType) {
        transaction {
            table.update({ getClassWhereClause(entityType) }) {
                it[myTable.touchedInLoading] = false
            }
        }
    }

    private fun getClassWhereClause(entityType: EntityType): Op<Boolean> = Op.build {
        myTable.entityType eq entityType
    }

    /**
     * Change entity current status to *REMOVED*
     *
     * @param entityType EntityType
     */
    fun markEntitiesAsRemove(entityType: EntityType) {
        transaction {
            table.update({
                getClassWhereClause(entityType)
                    .and(myTable.touchedInLoading eq false)
                    .and(myTable.currentStatus neq EntityStatus.REMOVED)
            }
            ) {
                it.update(myTable.previousStatus, myTable.currentStatus)
                it[myTable.currentStatus] = EntityStatus.REMOVED
                it[myTable.removed] = TIMESTAMP
            }
        }
    }

    /**
     * Remove entities from the DB. Only entities that absent more than max absent days will be removed.
     *
     * @param entityType EntityType The entity type
     * @param maxAge Int The maximum day of absent
     */
    fun removeOldEntities(entityType: EntityType, maxAge: Int) {
        transaction {
            find {
                getClassWhereClause(entityType).and(
                    (myTable.absentDays greaterEq maxAge)
                            and (myTable.currentStatus eq EntityStatus.REMOVED)
                )
            }.toList()
        }.forEach {
            transaction { it.delete() }
        }
    }

    /**
     * Recalculate and update entity absent days
     *
     * @param entityType EntityType The entity type
     */
    fun updateAbsentAge(entityType: EntityType) {
        transaction {
            find {
                getClassWhereClause(entityType).and(myTable.currentStatus eq EntityStatus.REMOVED)
            }.toList()
        }.forEach {
            val days = Duration(it.removed, TIMESTAMP).standardDays.toInt()
            transaction { it.absentDays = days }
        }
    }
}

/**
 * Build equal expression for where clause
 *
 * @param column Column<*> The column to compare
 * @param value Type The value to compare
 * @return Expression<Boolean>
 */
@Suppress("UNCHECKED_CAST")
fun equalsBuild(column: Column<*>, value: Type): Expression<Boolean> =
    when (column.columnType) {
        is VarCharColumnType -> Op.build { (column as Column<String>) eq (value.value as String) }
        is LongColumnType -> Op.build { (column as Column<Long>) eq (value.value as Long) }
        is DoubleColumnType -> Op.build { (column as Column<Double>) eq (value.value as Double) }
        is BooleanColumnType -> Op.build { (column as Column<Boolean>) eq (value.value as Boolean) }
        is DateColumnType -> Op.build { (column as Column<DateTime>) eq (value.value as DateTime) }
        else -> throw AppDatabaseException(
            "Cannot build equals operator for column type ${column.columnType.sqlType()}"
        )
    }
