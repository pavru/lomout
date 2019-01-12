package net.pototskiy.apps.magemediation.plugins.medium

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.database.onec.GroupEntity
import net.pototskiy.apps.magemediation.api.database.onec.GroupRelationEntityClass
import net.pototskiy.apps.magemediation.api.plugable.Plugable
import net.pototskiy.apps.magemediation.api.plugable.medium.GroupPathBuilder
import net.pototskiy.apps.magemediation.api.source.SourceFieldType

class GroupPathFromCodeBuilder : GroupPathBuilder {
    private lateinit var entities: GroupRelationEntityClass

    override fun setGroupEntities(entities: GroupRelationEntityClass) {
        this.entities = entities
    }

    override fun buildPath(entity: GroupEntity): String {
        val config = Plugable.config!!.mediator.onec.group.pathAttribute
        val path = mutableListOf<String>()
        var relationEntity = entities
            .findByAttribute(codeAttrName, codeAttrType, entity.groupCode)
            .firstOrNull()
            ?: throw DatabaseException("OneC group relation data has no information for group<${entity.groupCode}>")
        var name = entities.getAttribute(relationEntity, nameAttrName, nameAttrType).firstOrNull() as? String
        while (name != null) {
            path.add(name)
            val parent = entities.getAttribute(relationEntity, parentAttrName, parentAttrType)
                .firstOrNull() as? String
                ?: break
            if (parent.isBlank()) break
            relationEntity = entities.findByAttribute(codeAttrName, parentAttrType, parent)
                .firstOrNull()
                    ?: break
            name = entities.getAttribute(relationEntity, nameAttrName, nameAttrType)
                .firstOrNull() as? String
        }
        return path.reversed().joinToString(config.separator?.second ?: "/")
    }

    companion object {
        private const val codeAttrName = "group_code"
        private val codeAttrType = SourceFieldType.STRING
        private const val nameAttrName = "group_name"
        private val nameAttrType = SourceFieldType.STRING
        private const val parentAttrName = "group_parent_code"
        private val parentAttrType = SourceFieldType.STRING
    }
}
