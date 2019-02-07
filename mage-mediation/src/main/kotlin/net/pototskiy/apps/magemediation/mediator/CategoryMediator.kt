package net.pototskiy.apps.magemediation.mediator

import net.pototskiy.apps.magemediation.CONFIG_BUILDER
import net.pototskiy.apps.magemediation.api.MEDIATOR_LOG_LEVEL
import net.pototskiy.apps.magemediation.api.config.data.Attribute
import org.slf4j.LoggerFactory

class CategoryMediator : AbstractMediator {
    private val mediatorConfig = CONFIG_BUILDER.config.mediator
    private val log = LoggerFactory.getLogger(MEDIATOR_LOG_LEVEL)

    override fun merge() {
//        transaction { MediumCategories.deleteAll() }
//        transaction { MageCategory.all().toList() }.forEach {
//            try {
//                val mageID = getMageCategoryID(it)
//                val magePtah = getMageCategoryPath(it)
//                if (mageID == null) {
//                    throw MediationException("Magento medium has no ID or path")
//                }
//                val group: GroupEntity? = tryToFindOnecRelatedGroup(mageID, magePtah)
//                if (group == null) {
//                    createNewOnecGroupFromMage(mageID)
//                } else {
//                    updateOnecGroupFromMage(group, mageID)
//                }
//            } catch (e: Exception) {
//                log.error("Magento category mediation error: ${e.message}")
//            }
//        }
//        transaction { OnecGroup.all().toList() }.forEach {
//        }
    }

//    private fun createNewOnecGroupFromMage(mageID: Any) {
//        val idAttr = mediatorConfig.magento.category.idAttribute
//        val mageEntity = MageCategory.findByAttribute(idAttr, mageID).first()
//        val mageData = MageCategory.getAttributes(mageEntity)
//        val data = mapAndTransformMageAttributes(mageData)
//        MediumCategory.insertNewEntity(MediumDataTarget.ONEC, MediumDataState.CREATE, data)
//    }

    private fun mapAndTransformMageAttributes(mageData: Map<Attribute, Any>): Map<Attribute, Any> {
        val attrMaps = mediatorConfig.mapping.category.mageAttrToOnecAttr
        return mageData.map { entry ->
            val (attr, value) = entry
            attrMaps[attr]?.let {
                it.attribute to (it.transformer?.transform(value) ?: value)
            } ?: attr to value
        }.toMap()
    }

//    private fun updateOnecGroupFromMage(group: GroupEntity, mageID: Any) {
//        val idAttr = mediatorConfig.magento.category.idAttribute
//        val mageEntity = MageCategory.findByAttribute(idAttr, mageID).first()
//        val mageData = MageCategory.getAttributes(mageEntity)
//        val dataFromMage = mapAndTransformMageAttributes(mageData)
//        val onecIDAttr = mediatorConfig.onec.group.idAttribute
//        val dataFromFromOnec = OnecGroup.getAttributes(group)
//        val dataToUpdate = dataFromMage.mapNotNull { (attr, value) ->
//            if (value != dataFromFromOnec[attr]) {
//                attr to value
//            } else {
//                null
//            }
//        }.forEach { (attr, value) ->
//
//        }
//    }

//    private fun tryToFindOnecRelatedGroup(mageID: Any, magePath: String): GroupEntity? {
//        val idAttr = mediatorConfig.onec.group.idAttribute
//        val pathAttr = mediatorConfig.onec.group.pathAttribute
//        // Try to find OneC group with the Magento category ID
//        val groupByID = listOf(mageID)
//            .plus(mapMageIDToOnecID(mageID))
//            .plus(mapMagePathToOnecID(magePath))
//            .map { OnecGroup.findByAttribute(idAttr, it) }
//            .flatten()
//        // Try to find OneC group with the Magento category path
//        val groupByPath = listOf(magePath)
//            .plus(mapMageIDToOnecPath(magePath))
//            .plus(mapMagePathToOnecPath(magePath))
//            .map { path ->
//                if (!mediatorConfig.onec.group.pathAttribute.isSynthetic) {
//                    val groups = OnecGroup.findByAttribute(pathAttr, path)
//                    groups.filter { getOnecGroupPath(it) == magePath }
//                } else {
//                    transaction {
//                        OnecGroup.all()
//                            .filter {
//                                try {
//                                    getOnecGroupPath(it) == path
//                                } catch (e: Exception) {
//                                    log.error(e.message)
//                                    false
//                                }
//                            }
//                    }
//                }
//            }
//            .flatten()
//        if (groupByID.isNotEmpty()) {
//            val candidates = groupByID.intersect(groupByPath)
//            if (candidates.size > 1) {
//                log.warn("Magento category<id:$mageID,path:$magePath> fits to more than one OneC groups")
//            } else if (candidates.isEmpty()) {
//                log.warn("OneC groups<${groupByID.joinToString(", ") {
//                    OnecGroup.getAttribute(it, idAttr)?.toString() ?: ""
//                }}> fitted to magento category by ID but not by path")
//            } else if (candidates.size != 1 && groupByID.size > 1) {
//                log.warn("More than one OneC group found that fitted to Magento category<id:$mageID,path:$magePath>")
//            }
//            return candidates.firstOrNull() ?: groupByID.first()
//        } else if (groupByPath.isNotEmpty()) {
//            if (groupByPath.size > 1) {
//                log.warn("More than one OneC group is fitted to magento category by id")
//            }
//            return groupByPath.first()
//        }
//        return null
//    }

//    private fun mapMagePathToOnecPath(magePath: String): List<String> =
//        mediatorConfig.mapping.category.magePathToOnecPath[magePath]?.let {
//            listOf(it)
//        } ?: listOf()
//
//    private fun mapMageIDToOnecPath(magePath: String): List<String> =
//        mediatorConfig.mapping.category.mageIDtoOnecPath[magePath]?.let {
//            listOf(it)
//        } ?: listOf()
//
//    private fun mapMagePathToOnecID(path: String): List<Any> {
//        val id = mediatorConfig.mapping.category.magePathToOnecID[path]
//        return if (id == null) listOf() else listOf(id)
//    }
//
//    private fun mapMageIDToOnecID(id: Any): List<Any> {
//        val oneID = mediatorConfig.mapping.category.mageIDToOnecID[id]
//        return if (oneID == null) listOf() else listOf(oneID)
//    }
//
//    private fun getOnecGroupPath(entity: GroupEntity): String {
//        val attr = mediatorConfig.onec.group.pathAttribute
//        return OnecGroup.getAttribute(entity, attr) as? String ?: ""
//    }
//
//    private fun getMageCategoryPath(entity: CategoryEntity): String {
//        val attr = mediatorConfig.magento.category.pathAttribute
//        return MageCategory.getAttribute(entity, attr) as? String ?: ""
//    }
//
//    private fun getMageCategoryID(entity: CategoryEntity): Any? {
//        val idAttr = mediatorConfig.magento.category.idAttribute
//        return MageCategory.getAttribute(entity, idAttr)
//    }
}
