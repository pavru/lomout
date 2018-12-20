package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.IMPORT_DATETIME
import net.pototskiy.apps.magemediation.config.excel.Field
import net.pototskiy.apps.magemediation.config.excel.FieldType
import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttribute
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class DatabaseUpdater(private val tableSet: TargetTableSet) {
    fun update(data: Map<String, Any?>, headers: List<Field>) {
        var entity = tableSet.entity.findEntityByKeyFields(data)
        if (entity == null) {
            entity = insertNewRecord(data)
        } else {
            if (areThereNewMainData(entity, data))
                updateMainRecord(entity, data)
        }
        val mainColumns = tableSet.mainTableHeaders.map { it.name }
        val attrData = data.filter { it.key !in (mainColumns) }
        testAndUpdateAttributes(entity, attrData, headers)
    }

    private fun testAndUpdateAttributes(entity: VersionEntity, data: Map<String, Any?>, headers: List<Field>) {
        testAndUpdateIntAttributes(entity, data, headers)
        testAndUpdateDoubleAttributes(entity, data, headers)
        testAndUpdateBoolAttributes(entity, data, headers)
        testAndUpdateDateAttributes(entity, data, headers)
        testAndUpdateDatetimeAttributes(entity, data, headers)
        testAndUpdateTextAtrributes(entity, data, headers)
        testAndUpdateStringAttributes(entity, data, headers)
    }

    private fun testAndUpdateStringAttributes(
        entity: VersionEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        testAndUpdateTypedAttributes(
            listOf(FieldType.STRING, FieldType.STRING_LIST),
            tableSet.varcharAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateTextAtrributes(
        entity: VersionEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        testAndUpdateTypedAttributes(
            listOf(FieldType.TEXT),
            tableSet.textAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateDatetimeAttributes(
        entity: VersionEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        testAndUpdateTypedAttributes(
            listOf(FieldType.DATETIME, FieldType.DATETIME_LIST),
            tableSet.datetimeAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateDateAttributes(
        entity: VersionEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        testAndUpdateTypedAttributes(
            listOf(FieldType.DATE, FieldType.DATE_LIST),
            tableSet.dateAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateBoolAttributes(
        entity: VersionEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        testAndUpdateTypedAttributes(
            listOf(FieldType.BOOL, FieldType.BOOL_LIST),
            tableSet.boolAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateDoubleAttributes(
        entity: VersionEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        testAndUpdateTypedAttributes(
            listOf(FieldType.DOUBLE, FieldType.DOUBLE_LIST),
            tableSet.doubleAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateIntAttributes(
        entity: VersionEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        testAndUpdateTypedAttributes(
            listOf(FieldType.INT, FieldType.INT_LIST),
            tableSet.intAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateTypedAttributes(
        fieldType: List<FieldType>,
        attrTable: TypedAttributeEntityClass<*, *>,
        entity: VersionEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        headers.filter { it.type in fieldType }.forEach { field ->
            attrTable.table as TypedAttribute
            val current = transaction {
                attrTable.find {
                    ((attrTable.table.product eq entity.id)
                            and (attrTable.table.code eq field.name))
                }.toList()
            }
            var newValue = if (field.type in fieldType) data[field.name] else null
            if (current.count() == 0 && newValue != null) {
                if (newValue !is List<*>) {
                    newValue = listOf(newValue)
                }
                newValue as List<*>
                var listPosition = 0
                newValue.forEach { valueToSet ->
                    transaction {
                        attrTable.new {
                            product = entity
                            code = field.name
                            index = listPosition++
                            setValue(valueToSet!!)
                        }
                    }
                }
                if (newValue.count() > 0) {
                    entity.setUpdateDatetime(IMPORT_DATETIME)
                }
            } else if (current.count() != 0 && newValue != null) {
                val needToUpdate: Boolean = if (!field.type.isList) {
                    current.first().compareTo(newValue) != 0
                } else {
                    newValue as List<*>
                    if (current.count() != newValue.count()) {
                        true
                    } else {
                        val list = newValue as List<*>
                        current.all { c -> list.any { c.compareTo(it!!) == 0 } } == false || current.count() != newValue.count()
                    }
                }
                if (needToUpdate) {
                    transaction {
                        attrTable.find {
                            ((attrTable.table.product eq entity.id)
                                    and (attrTable.table.code eq field.name))
                        }.toList()
                            .forEach { it.delete() }
                    }
                    if (newValue !is List<*>) {
                        newValue = listOf(newValue)
                    }
                    newValue as List<*>
                    var listPosition = 0
                    newValue.forEach { valueToSet ->
                        transaction {
                            attrTable.new {
                                product = entity
                                code = field.name
                                index = listPosition++
                                setValue(valueToSet!!)
                            }
                        }
                    }
                    entity.setUpdateDatetime(IMPORT_DATETIME)
                }
            } else if (current.count() != 0 && newValue == null) {
                var count: Int = 0
                transaction {
                    val list = attrTable.find {
                        ((attrTable.table.product eq entity.id)
                                and (attrTable.table.code eq field.name))
                    }.toList()
                    count = list.count()
                    list.forEach { it.delete() }
                }
                if (count != 0) {
                    entity.setUpdateDatetime(IMPORT_DATETIME)
                }
            }
        }
    }

    private fun areThereNewMainData(entity: VersionEntity, data: Map<String, Any?>): Boolean =
        entity.mainDataIsEqual(data)

    private fun updateMainRecord(entity: VersionEntity, data: Map<String, Any?>) {
        entity.updateMainRecord(data)
        entity.setUpdateDatetime(IMPORT_DATETIME)
    }

    private fun insertNewRecord(data: Map<String, Any?>): VersionEntity {
        return tableSet.entity.insertNewRecord(data, IMPORT_DATETIME)
    }
}
