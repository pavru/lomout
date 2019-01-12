package net.pototskiy.apps.magemediation.mediator

import net.pototskiy.apps.magemediation.CONFIG
import net.pototskiy.apps.magemediation.api.MEDIATOR_LOG_LEVEL
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.toSourceFieldType
import net.pototskiy.apps.magemediation.api.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.api.database.mage.CategoryEntity
import net.pototskiy.apps.magemediation.api.database.onec.GroupEntity
import net.pototskiy.apps.magemediation.api.source.isColumnTypeCompatible
import net.pototskiy.apps.magemediation.database.mage.MageCategory
import net.pototskiy.apps.magemediation.database.mediation.category.MediumCategories
import net.pototskiy.apps.magemediation.database.onec.OnecGroup
import net.pototskiy.apps.magemediation.database.onec.OnecGroupRelation
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import kotlin.reflect.full.createInstance

class CategoryMediator : AbstractMediator {
    private val mediatorConfig = CONFIG.config.mediator
    private val log = LoggerFactory.getLogger(MEDIATOR_LOG_LEVEL)

    override fun merge() {
        transaction { MediumCategories.deleteAll() }
        transaction {
            MageCategory.all().forEach {
                try {
                    val mageID = getMageCategoryID(it)
                    val magePtah = getMageCategoryPath(it)
                    if (mageID == null) {
                        throw MediationException("Magento medium has no ID or path")
                    }
                    val group: GroupEntity = tryToFindOnecRelatedGroup(mageID, magePtah)
                } catch (e: Exception) {
                    log.error("Magento category mediation error: ${e.message}")
                }
            }
            OnecGroup.all().forEach {
            }
        }
    }

    private fun tryToFindOnecRelatedGroup(mageID: Any, magePtah: String): GroupEntity {
        val idName = mediatorConfig.onec.group.idAttribute.name
        val idType = mediatorConfig.onec.group.idAttribute.type.toSourceFieldType()
        val pathName = mediatorConfig.onec.group.pathAttribute.name
        val pathType = mediatorConfig.onec.group.pathAttribute.type.toSourceFieldType()
        var groupByID = OnecGroup.findByAttribute(idName, idType, mageID)
        var groupByPath: List<GroupEntity>?
        if (!mediatorConfig.onec.group.pathAttribute.synthetic) {
            val groups = OnecGroup.findByAttribute(pathName, pathType, magePtah)
            groupByPath = groups.filter {
                val path = getOnecGroupPath(it)
                path == magePtah
            }
        } else {
            val found = mutableListOf<GroupEntity>()
            transaction {
                OnecGroup.all().forEach {
                    try {
                        val path = getOnecGroupPath(it)
                        if (path == magePtah) {
                            found.add(it)
                        }
                    } catch (e: Exception) {
                        log.error("Magento category mediation error: ${e.message}")
                    }
                }
            }
            groupByPath = found.toList()
        }
        if (groupByID.isNotEmpty()) {
            val candidates = groupByID.intersect(groupByPath)
            if (candidates.size > 1) {
                log.warn("Magento category<id:$mageID,path:$magePtah> fits to more than one OneC groups")
            } else if (candidates.isEmpty()) {
                log.warn("OneC groups<${groupByID.joinToString(", ") { it.groupCode }} fit by ID but not by path>")
            } else if (candidates.size != 1 && groupByID.size > 1) {
                log.warn("More than one OneC group found that fited to Magento category<id:$mageID,path:$magePtah>")
            }
        }
        TODO()
    }

    private fun getOnecGroupPath(entity: GroupEntity): String {
        val config = mediatorConfig.onec.group.pathAttribute
        var path = if (config.synthetic) {
            val builder = config.builder?.createInstance()
                ?: throw ConfigException("OneC group path builder can not be instanced")
            builder.setGroupEntities(OnecGroupRelation.Companion)
            builder.buildPath(entity)
        } else {
            val attrName = config.name
            val attrType = config.type.toSourceFieldType()
            OnecGroup.getAttribute(entity, attrName, attrType).firstOrNull() as? String
        }
        config.separator?.let {
            if (it.first.isNotEmpty() && it.second.isNotEmpty()) {
                path = path?.replace(it.first, it.second)
            }
        }
        config.root?.let {
            path = "$it${config.separator?.second ?: "/"}$path"
        }
        return path ?: ""
    }

    private fun getMageCategoryPath(entity: CategoryEntity): String {
        val config = mediatorConfig.magento.category.pathAttribute
        var path = if (config.synthetic) {
            val builder = config.builder?.createInstance()
                ?: throw ConfigException("Magneto medium path builder can not be instanced")
            builder.setCategoryEntities(MageCategory.Companion)
            builder.buildPath(entity)
        } else {
            val type = mediatorConfig.magento.category.pathAttribute.type.toSourceFieldType()
            val attrName = mediatorConfig.magento.category.pathAttribute.name
            val attrClass = MageCategory.getAttrEntityClassFor(type)
            val table = attrClass?.table as? TypedAttributeTable<*>
            if (attrClass != null && table != null) {
                transaction {
                    attrClass.find {
                        ((table.owner eq entity.id) and (table.code eq attrName))
                    }.firstOrNull()?.value as? String
                }
            } else {
                throw ConfigException("Magento medium path attribute type is not supported")
            }
        }
        config.separator?.let {
            if (it.first.isNotEmpty() && it.second.isNotEmpty()) {
                path = path?.replace(it.first, it.second)
            }
        }
        config.root?.let {
            path = "$it${config.separator?.second ?: "/"}$path"
        }
        return path ?: ""
    }

    private fun getMageCategoryID(entity: CategoryEntity): Any? {
        val type = mediatorConfig.magento.category.idAttribute.type.toSourceFieldType()
        val attrName = mediatorConfig.magento.category.idAttribute.name
        val mainTableColumn = MageCategory.mainTableHeaders.find { it.name == attrName }
        if (mainTableColumn != null) {
            if (!type.isColumnTypeCompatible(mainTableColumn.columnType)) {
                throw ConfigException("Magento medium id attribute exists but its type is uncompilable")
            }
            return entity.readValues[mainTableColumn]
        } else {
            val attrClass = MageCategory.getAttrEntityClassFor(type)
                ?: throw ConfigException("Magento medium id attribute type is not supported")
            val table = attrClass.table as TypedAttributeTable<*>
            val id = transaction {
                attrClass.find {
                    ((table.owner eq entity.id) and (table.code eq attrName))
                }.toList().firstOrNull()
            }
            return id?.value
        }
    }
}