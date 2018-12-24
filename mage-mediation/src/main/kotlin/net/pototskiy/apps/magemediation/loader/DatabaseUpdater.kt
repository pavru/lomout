package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.IMPORT_DATETIME
import net.pototskiy.apps.magemediation.config.excel.Field
import net.pototskiy.apps.magemediation.config.excel.FieldType
import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.source.SourceDataEntity
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
        val attrData = data.filter { it.key !in mainColumns }
        testAndUpdateAttributes(entity, attrData, headers)
    }

    private fun testAndUpdateAttributes(entity: SourceDataEntity, data: Map<String, Any?>, headers: List<Field>) {
        testAndUpdateIntAttributes(entity, data, headers)
        testAndUpdateDoubleAttributes(entity, data, headers)
        testAndUpdateBoolAttributes(entity, data, headers)
        testAndUpdateDateAttributes(entity, data, headers)
        testAndUpdateDatetimeAttributes(entity, data, headers)
        testAndUpdateTextAttributes(entity, data, headers)
        testAndUpdateStringAttributes(entity, data, headers)
    }

    private fun testAndUpdateStringAttributes(
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        if (tableSet.varcharAttributes == null) {
            return
        }
        testAndUpdateTypedAttributes(
            listOf(FieldType.STRING, FieldType.STRING_LIST),
            tableSet.varcharAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateTextAttributes(
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        if (tableSet.textAttributes == null) {
            return
        }
        testAndUpdateTypedAttributes(
            listOf(FieldType.TEXT),
            tableSet.textAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateDatetimeAttributes(
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        if (tableSet.datetimeAttributes == null) {
            return
        }
        testAndUpdateTypedAttributes(
            listOf(FieldType.DATETIME, FieldType.DATETIME_LIST),
            tableSet.datetimeAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateDateAttributes(
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        if (tableSet.dateAttributes == null) {
            return
        }
        testAndUpdateTypedAttributes(
            listOf(FieldType.DATE, FieldType.DATE_LIST),
            tableSet.dateAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateBoolAttributes(
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        if (tableSet.boolAttributes == null) {
            return
        }
        testAndUpdateTypedAttributes(
            listOf(FieldType.BOOL, FieldType.BOOL_LIST),
            tableSet.boolAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateDoubleAttributes(
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        if (tableSet.doubleAttributes == null) {
            return
        }
        testAndUpdateTypedAttributes(
            listOf(FieldType.DOUBLE, FieldType.DOUBLE_LIST),
            tableSet.doubleAttributes,
            entity,
            data,
            headers
        )
    }

    private fun testAndUpdateIntAttributes(
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        if (tableSet.intAttributes == null) {
            return
        }
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
        entityClass: TypedAttributeEntityClass<*, *>,
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<Field>
    ) {
        headers.filter { it.type in fieldType }.forEach { field ->
            val table = entityClass.table as TypedAttributeTable<*>
            val current = getCurrentEntity(entityClass, table, entity, field)
            val newValue = if (field.type in fieldType) data[field.name] else null
            val thereIsNewValue = newValue != null && (newValue !is List<*> || newValue.isNotEmpty())
            if (current.count() == 0 && thereIsNewValue) {
                addNewAttribute(newValue!!, entityClass, entity, field)
            } else if (current.count() != 0 && thereIsNewValue) {
                updateExistingAttribute(field, current, newValue!!, entityClass, table, entity)
            } else if (current.count() != 0 && !thereIsNewValue) {
                removeExistingAttribute(entityClass, table, entity, field)
            }
        }
    }

    private fun removeExistingAttribute(
        attrTable: TypedAttributeEntityClass<*, *>,
        table: TypedAttributeTable<*>,
        entity: SourceDataEntity,
        field: Field
    ) {
        var count = 0
        transaction {
            val list = attrTable.find {
                ((table.owner eq entity.id)
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
        entity: SourceDataEntity
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
                !current.all { c ->
                    list.any { c.compareToWithTypeCheck(it!!) == 0 }
                } || current.count() != newValue1.count()
            }
        }
        if (needToUpdate) {
            transaction {
                attrTable.find {
                    ((table.owner eq entity.id)
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
                        owner = entity
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
        entity: SourceDataEntity,
        field: Field
    ) {
        var newValue1 = newValue
        if (newValue1 !is List<*>) {
            newValue1 = listOf(newValue1)
        }
        newValue1 as List<*>
        var listPosition = 0
        try {
            newValue1.forEach { valueToSet ->
                transaction {
                    attrTable.new {
                        owner = entity
                        code = field.name
                        index = listPosition++
                        setValueWithTypeCheck(valueToSet!!)
                    }
                }
            }
        } catch (e: Exception) {
            throw LoaderException("Attribute<${field.name}> with value<${newValue1.joinToString(", ")}> can not be added")
        }
        if (newValue1.count() > 0) {
            entity.setUpdateDatetime(IMPORT_DATETIME)
        }
    }

    private fun getCurrentEntity(
        attrEntityClass: TypedAttributeEntityClass<*, *>,
        table: TypedAttributeTable<*>,
        entity: SourceDataEntity,
        field: Field
    ): List<TypedAttributeEntity<*>> {
        return transaction {
            attrEntityClass.find {
                ((table.owner eq entity.id)
                        and (table.code eq field.name))
            }.toList().sortedBy { it.index }
        }
    }

    private fun areThereNewMainData(entity: SourceDataEntity, data: Map<String, Any?>): Boolean =
        entity.mainDataIsNotEqual(data)

    private fun updateMainRecord(entity: SourceDataEntity, data: Map<String, Any?>) {
        entity.updateMainRecord(data)
        entity.setUpdateDatetime(IMPORT_DATETIME)
    }

    private fun insertNewRecord(data: Map<String, Any?>): SourceDataEntity {
        return tableSet.entity.insertNewRecord(data, IMPORT_DATETIME)
    }
}
