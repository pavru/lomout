package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.config.loader.dataset.FieldConfiguration
import net.pototskiy.apps.magemediation.api.config.toSourceFieldType
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntity
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntityClass
import net.pototskiy.apps.magemediation.api.source.SourceFieldType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseUpdater(private val table: SourceDataEntityClass<*>) {

    fun update(data: Map<String, Any?>, headers: List<FieldConfiguration>) {
        var newEntityWasCreated = false
        var entity = table.findEntityByKeyFields(data)
        entity?.wasUnchanged()
        if (entity == null) {
            entity = insertNewRecord(data)
            newEntityWasCreated = true
        } else {
            if (areThereNewMainData(entity, data))
                updateMainRecord(entity, data)
        }
        val mainColumns = table.mainTableHeaders.map { it.name }
        val attrData = data.filter { it.key !in mainColumns }
        val attrHeaders = headers.filter {
            it.name !in mainColumns && it.type.toSourceFieldType() != SourceFieldType.ATTRIBUTE_LIST
        }
        testAndUpdateTypedAttributes(entity, attrData, attrHeaders)
        if (newEntityWasCreated) {
            entity.wasCreated()
        }
    }

    private fun testAndUpdateTypedAttributes(
        entity: SourceDataEntity,
        data: Map<String, Any?>,
        headers: List<FieldConfiguration>
    ) {
        for (v in SourceFieldType.values().filter { it != SourceFieldType.ATTRIBUTE_LIST }) {
            val attrEntityClass = table.getAttrEntityClassFor(v)
            headers.filter { it.type.toSourceFieldType() == v }.forEach { field ->
                if (attrEntityClass == null) {
                    throw LoaderException("Field type<${v.name}> does not support by mediation database")
                }
                val attrTable = attrEntityClass.table as TypedAttributeTable<*>
                val current = transaction {
                    attrEntityClass.find {
                        ((attrTable.code eq field.name) and (attrTable.owner eq entity.id))
                    }.toList()
                }.sortedBy { it.index }
                val newValue = data[field.name]
                val thereIsNewValue = newValue != null && (newValue !is List<*> || newValue.isNotEmpty())
                if (current.count() == 0 && thereIsNewValue) {
                    addNewAttribute(newValue!!, attrEntityClass, entity, field)
                } else if (current.count() != 0 && thereIsNewValue) {
                    updateExistingAttribute(newValue!!, attrEntityClass, entity, field, current)
                } else if (current.count() != 0 && !thereIsNewValue) {
                    removeExistingAttribute(attrEntityClass, entity, field)
                }
            }
        }
    }

    private fun removeExistingAttribute(
        attrEntityClass: TypedAttributeEntityClass<*, *>,
        entity: SourceDataEntity,
        field: FieldConfiguration
    ) {
        val table = attrEntityClass.table as TypedAttributeTable<*>
        var count = 0
        transaction {
            val list = attrEntityClass.find {
                ((table.owner eq entity.id)
                        and (table.code eq field.name))
            }.toList()
            count = list.count()
            list.forEach { it.delete() }
        }
        if (count != 0) {
            entity.wasUpdated()
        }
    }

    private fun updateExistingAttribute(
        newValue: Any,
        attrEntityClass: TypedAttributeEntityClass<*, *>,
        entity: SourceDataEntity,
        field: FieldConfiguration,
        current: List<TypedAttributeEntity<*>>
    ) {
        val table = attrEntityClass.table as TypedAttributeTable<*>
        val needToUpdate = if (newValue !is List<*>) {
            current.first().value != newValue
        } else {
            current.count() != newValue.count() || !current.all { c -> newValue.any { c.value == it } }
        }

        if (needToUpdate) {
            transaction {
                attrEntityClass.find {
                    ((table.owner eq entity.id) and (table.code eq field.name))
                }.toList().forEach { it.delete() }
            }
            var listPosition = 0
            newValue.let { if (it is List<*>) it else listOf(it) }.forEach { valueToSet ->
                transaction {
                    attrEntityClass.new {
                        owner = entity.id
                        code = field.name
                        index = listPosition++
                        setValueWithTypeCheck(valueToSet!!)
                    }
                }
            }
            entity.wasUpdated()
        }
    }

    private fun addNewAttribute(
        newValue: Any,
        attrTable: TypedAttributeEntityClass<*, *>,
        entity: SourceDataEntity,
        field: FieldConfiguration
    ) {
        var newValueList = newValue
        if (newValueList !is List<*>) {
            newValueList = listOf(newValueList)
        }
        newValueList as List<*>
        var listPosition = 0
        try {
            newValueList.forEach { valueToSet ->
                transaction {
                    attrTable.new {
                        owner = entity.id
                        code = field.name
                        index = listPosition++
                        setValueWithTypeCheck(valueToSet!!)
                    }
                }
            }
        } catch (e: Exception) {
            throw LoaderException("Attribute<${field.name}> with value<${newValueList.joinToString(", ")}> can not be added")
        }
        if (newValueList.count() > 0) {
            entity.wasUpdated()
        }
    }

    private fun areThereNewMainData(entity: SourceDataEntity, data: Map<String, Any?>): Boolean =
        entity.isNotEqual(data)

    private fun updateMainRecord(entity: SourceDataEntity, data: Map<String, Any?>) {
        entity.updateEntity(data)
        entity.wasUpdated()
    }

    private fun insertNewRecord(data: Map<String, Any?>): SourceDataEntity {
        return table.insertNewRecord(data)
    }
}
