package net.pototskiy.apps.magemediation.plugins.medium

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.api.database.mage.CategoryTable
import net.pototskiy.apps.magemediation.api.plugable.Plugable
import net.pototskiy.apps.magemediation.api.plugable.medium.CategoryPathBuilder
import net.pototskiy.apps.magemediation.api.source.SourceFieldType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryPathFromRelationBuilder : CategoryPathBuilder {
    private lateinit var entities: CategoryEntityClass
    private val config = Plugable.config!!.mediator.magento.category.pathAttribute

    override fun setCategoryEntities(entities: CategoryEntityClass) {
        this.entities = entities
    }

    override fun buildPath(entity: CategoryEntity): String {
        val path = mutableListOf<String>()
        var current: CategoryEntity? = entity
        var name = getName(entity)
        while (name != null) {
            path.add(name)
            current = current?.let { getParent(it) }
            name = current?.let { getName(it) }
        }
        return path.reversed().joinToString(config.separator?.second ?: "/")
    }

    private fun getParent(current: CategoryEntity): CategoryEntity? {
        val attrClass = entities.getAttrEntityClassFor(parentAttrType)
        val table = attrClass?.table as? TypedAttributeTable<*>
            ?: throw DatabaseException("Category attribute with type<${parentAttrType.name}> can not be found")
        val parentID = transaction {
            attrClass.find {
                ((table.owner eq current.id) and (table.code eq parentAttrName))
            }.firstOrNull()?.value
        }
        return parentID?.let {
            entities.find {
                val t = this@CategoryPathFromRelationBuilder.entities.table as CategoryTable
                t.entityID eq (it as Long)
            }.firstOrNull()
        }
    }

    private fun getName(entity: CategoryEntity): String? {
        val attrClass = entities.getAttrEntityClassFor(nameAttrType)
        val table = attrClass?.table as? TypedAttributeTable<*>
            ?: throw DatabaseException("Category attribute with type<${nameAttrType.name}> can not be found")
        return transaction {
            attrClass.find {
                ((table.owner eq entity.id) and (table.code eq nameAttrName))
            }.firstOrNull()?.value as? String
        }
    }

    companion object {
        private val nameAttrType = SourceFieldType.STRING
        private const val nameAttrName = "name"
        private val parentAttrType = SourceFieldType.INT
        private val parentAttrName = "parent_id"
    }
}