package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.IMPORT_DATETIME
import net.pototskiy.apps.magemediation.config.excel.Field
import net.pototskiy.apps.magemediation.config.excel.FieldType
import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

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
        testAndUpdateTextAttributes(entity, data, headers)
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

    private fun testAndUpdateTextAttributes(
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
            attrTable.table as TypedAttributeTable<*>
            val current = getCurrentEntity(attrTable, attrTable.table, entity, field)
            val newValue = if (field.type in fieldType) data[field.name] else null
            if (current.count() == 0 && newValue != null) {
                addNewAttribute(newValue, attrTable, entity, field)
            } else if (current.count() != 0 && newValue != null) {
                updateExistingAttribute(field, current, newValue, attrTable, attrTable.table, entity)
            } else if (current.count() != 0 && newValue == null) {
                removeExistingAttribute(attrTable, attrTable.table, entity, field)
            }
        }
    }

    private fun removeExistingAttribute(
        attrTable: TypedAttributeEntityClass<*, *>,
        table: TypedAttributeTable<*>,
        entity: VersionEntity,
        field: Field
    ) {
        var count = 0
        transaction {
            val list = attrTable.find {
                ((table.product eq entity.id)
                        and (table.code eq field.name))
            }.toList()
            count = list.count()
            list.forEach { it.delete() }
        }
        if (count != 0) {
            entity.setUpdateDatetime(IMPORT_DATETIME)
        }
    }

    private fun updateExistingAttribute(
        field: Field,
        current: List<TypedAttributeEntity<*>>,
        newValue: Any,
        attrTable: TypedAttributeEntityClass<*, *>,
        table: TypedAttributeTable<*>,
        entity: VersionEntity
    ) {
        var newValue1 = newValue
        val needToUpdate: Boolean = if (!field.type.isList) {
            current.first().compareToWithTypeCheck(newValue1) != 0
        } else {
            newValue1 as List<*>
            if (current.count() != newValue1.count()) {
                true
            } else {
                val list = newValue1
                !current.all { c -> list.any { c.compareToWithTypeCheck(it!!) == 0 } } || current.count() != newValue1.count()
            }
        }
        if (needToUpdate) {
            transaction {
                attrTable.find {
                    ((table.product eq entity.id)
                            and (table.code eq field.name))
                }.toList()
                    .forEach { it.delete() }
            }
            if (newValue1 !is List<*>) {
                newValue1 = listOf(newValue1)
            }
            newValue1 as List<*>
            var listPosition = 0
            newValue1.forEach { valueToSet ->
                transaction {
                    attrTable.new {
                        product = entity
                        code = field.name
                        index = listPosition++
                        setValueWithTypeCheck(valueToSet!!)
                    }
                }
            }
            entity.setUpdateDatetime(IMPORT_DATETIME)
        }
    }

    private fun addNewAttribute(
        newValue: Any,
        attrTable: TypedAttributeEntityClass<*, *>,
        entity: VersionEntity,
        field: Field
    ) {
        var newValue1 = newValue
        if (newValue1 !is List<*>) {
            newValue1 = listOf(newValue1)
        }
        newValue1 as List<*>
        var listPosition = 0
        newValue1.forEach { valueToSet ->
            transaction {
                attrTable.new {
                    product = entity
                    code = field.name
                    index = listPosition++
                    setValueWithTypeCheck(valueToSet!!)
                }
            }
        }
        if (newValue1.count() > 0) {
            entity.setUpdateDatetime(IMPORT_DATETIME)
        }
    }

    private fun getCurrentEntity(
        attrTable: TypedAttributeEntityClass<*, *>,
        table: TypedAttributeTable<*>,
        entity: VersionEntity,
        field: Field
    ): List<TypedAttributeEntity<*>> {
        return transaction {
            attrTable.find {
                ((table.product eq entity.id)
                        and (table.code eq field.name))
            }.toList()
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
