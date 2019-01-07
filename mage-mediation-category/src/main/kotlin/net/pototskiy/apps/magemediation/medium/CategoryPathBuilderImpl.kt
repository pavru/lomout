package net.pototskiy.apps.magemediation.medium

import net.pototskiy.apps.magemediation.config.newOne.Config
import net.pototskiy.apps.magemediation.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.database.mage.CategoryEntityClass
import net.pototskiy.apps.magemediation.database.onec.GroupEntity
import net.pototskiy.apps.magemediation.database.onec.GroupEntityClass
import net.pototskiy.apps.magemediation.database.onec.GroupTable
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryPathBuilderImpl(
    private val mageCategories: CategoryEntityClass,
    private val onecGroups: GroupEntityClass,
    private val config: Config
) : CategoryPathBuilder {
    override fun buildNamePath(entity: GroupEntity, separator: String): String {
        val pathElements = getLinkedGroups(entity)
        return "$separator${pathElements.joinToString(separator) { it.groupName }}"
    }

    private fun getLinkedGroups(entity: GroupEntity): List<GroupEntity> {
        val elements = mutableListOf(entity)
        var parent: GroupEntity? = entity
        config.mediator.onec.group.let { groupMediatorConfiguration ->
            val regex = Regex(groupMediatorConfiguration.codeStructure)
            val filler = groupMediatorConfiguration.subGroupFiller
            var parentCode = getParentGroupCode(parent!!.groupCode, regex, filler)
            while (parentCode != null) {
                parent = transaction {
                    onecGroups.find {
                        (onecGroups.table as GroupTable).groupCode eq parentCode!!
                    }.firstOrNull()
                }
                if (parent != null) {
                    elements.add(parent!!)
                    parentCode = getParentGroupCode(parent!!.groupCode, regex, filler)
                } else {
                    break
                }
            }

        }
        return elements.reversed()
    }

    private fun getParentGroupCode(groupCode: String, regex: Regex, filler: String): String? {
        val groups = regex.matchEntire(groupCode)?.groups ?: return null
        for (v in groups.reversed().take(groups.size - 2)) {
            if (v != null) {
                if (groupCode.substring(v.range) == filler.repeat(v.value.count())) {
                    continue
                } else {
                    return groupCode.replaceRange(v.range, filler.repeat(v.value.count()))
                }
            } else {
                throw MediatorException("Group code<$groupCode> does not match code structure<${regex.pattern}>")
            }
        }
        return null
    }

    override fun buildCodePath(entity: GroupEntity, separator: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buildNamePath(entity: CategoryEntity, separator: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buildCodePath(entity: CategoryEntity, separator: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}