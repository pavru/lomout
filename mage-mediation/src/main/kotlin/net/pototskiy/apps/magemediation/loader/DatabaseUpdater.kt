package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.api.LOADER_LOG_NAME
import net.pototskiy.apps.magemediation.api.config.data.Field
import net.pototskiy.apps.magemediation.api.config.type.AttributeAttributeListType
import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.api.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntity
import net.pototskiy.apps.magemediation.api.database.source.SourceDataEntityClass
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseUpdater(private val entityClass: SourceDataEntityClass<*>) {
    private val logger = LogManager.getLogger(LOADER_LOG_NAME)

    fun update(data: Map<Attribute, Any>, headers: Map<Field, Attribute>) {
        var entity = entityClass.findEntityByKeyFields(data)
        entity?.wasUnchanged()
        if (entity == null) {
            entity = entityClass.insertNewRecord(data)
            entity.wasCreated()
        } else {
            testAndUpdateTypedAttributes(
                entity,
                data,
                headers.filterNot { it.value.key || it.value.type is AttributeAttributeListType }
            )
        }
    }

    private fun testAndUpdateTypedAttributes(
        entity: SourceDataEntity<*>,
        data: Map<Attribute, Any?>,
        headers: Map<Field, Attribute>
    ) {
        @Suppress("UNCHECKED_CAST")
        val storeData = entityClass.getAttributes(entity)
            .filter { it.key.name in headers.values.map { it.name } }
            .map { entry ->
                val attr = headers.values.findLast { it.name == entry.key.name }
                if (attr == null) {
                    logger.warn("${entityClass::class.simpleName} has stored attribute<$entity.key> that is not defined in loaded data")
                }
                attr to entry.value
            }
            .filter { it.first != null }
            .toMap() as Map<Attribute, Any?>
        headers.values.filter { !it.key }.forEach { attrDesc ->
            if (data.containsKey(attrDesc) && !storeData.containsKey(attrDesc)) {
                entityClass.addAttribute(entity, attrDesc, data[attrDesc]!!)
                entity.wasUpdated()
            } else if (data.containsKey(attrDesc) && storeData.containsKey(attrDesc) && data[attrDesc] != storeData[attrDesc]) {
                entityClass.updateAttribute(entity, attrDesc, data[attrDesc]!!)
                entity.wasUpdated()
            } else if (!data.containsKey(attrDesc) && storeData.containsKey(attrDesc)) {
                entityClass.removeAttribute(entity, attrDesc)
                entity.wasUpdated()
            }
        }
//        for (v in SourceFieldType.values().filter { it != SourceFieldType.ATTRIBUTE_LIST }) {
//            val attrEntityClass = entityClass.getAttrEntityClassFor(v)
//            headers.filter { it.type.toSourceFieldType() == v }.forEach { field ->
//                if (attrEntityClass == null) {
//                    throw LoaderException("Field type<${v.name}> does not support by mediation database")
//                }
//                val attrTable = attrEntityClass.table as TypedAttributeTable<*>
//                val current = transaction {
//                    attrEntityClass.find {
//                        ((attrTable.code eq field.name) and (attrTable.owner eq entity.id))
//                    }.toList()
//                }.sortedBy { it.index }
//                val newValue = data[field]
//                val thereIsNewValue = newValue != null && (newValue !is List<*> || newValue.isNotEmpty())
//                if (current.count() == 0 && thereIsNewValue) {
//                    addNewAttribute(newValue!!, attrEntityClass, entity, field)
//                } else if (current.count() != 0 && thereIsNewValue) {
//                    updateExistingAttribute(newValue!!, attrEntityClass, entity, field, current)
//                } else if (current.count() != 0 && !thereIsNewValue) {
//                    removeExistingAttribute(attrEntityClass, entity, field)
//                }
//            }
//        }
    }

    private fun removeExistingAttribute(
        attrEntityClass: TypedAttributeEntityClass<*, *>,
        entity: SourceDataEntity<*>,
        field: Field
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
        entity: SourceDataEntity<*>,
        field: Field,
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
        entity: SourceDataEntity<*>,
        field: Field
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

}
