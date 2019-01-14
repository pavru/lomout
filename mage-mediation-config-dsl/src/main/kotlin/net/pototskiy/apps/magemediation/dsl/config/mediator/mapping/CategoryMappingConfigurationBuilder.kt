package net.pototskiy.apps.magemediation.dsl.config.mediator.mapping

import net.pototskiy.apps.magemediation.api.config.mediator.mapping.CategoryMappingConfiguration
import net.pototskiy.apps.magemediation.dsl.config.ConfigDsl

@ConfigDsl
class CategoryMappingConfigurationBuilder {
    private val mapMageIDToOnecID = mutableMapOf<Any, Any>()
    private val mapMageIDToOnecPath = mutableMapOf<Any, String>()
    private val mapOnecIDToMageID = mutableMapOf<Any, Any>()
    private val mapOnecIDToMagePath = mutableMapOf<Any, String>()
    private val mapMagePathToOnecID = mutableMapOf<String, Any>()
    private val mapMagePathToOnecPath = mutableMapOf<String, String>()
    private val mapOnecPathToMageID = mutableMapOf<String, Any>()
    private val mapOnecPathToMagePath = mutableMapOf<String, String>()

    @Suppress("unused")
    fun CategoryMappingConfigurationBuilder.mageID(id: String) = MageID(id)

    @Suppress("unused")
    fun CategoryMappingConfigurationBuilder.mageID(id: Long) = MageID(id)

    @Suppress("unused")
    fun CategoryMappingConfigurationBuilder.onecID(id: String) = OnecID(id)

    @Suppress("unused")
    fun CategoryMappingConfigurationBuilder.onecID(id: Long) = OnecID(id)

    @Suppress("unused")
    fun CategoryMappingConfigurationBuilder.magePath(path: String) = MagePath(path)

    @Suppress("unused")
    fun CategoryMappingConfigurationBuilder.onecPath(path: String) = OnecPath(path)

    infix fun MageID.to(id: OnecID) = mapMageIDToOnecID.put(this.id, id.id)

    infix fun MageID.to(path: OnecPath) = mapMageIDToOnecPath.put(this.id, path.path)

    infix fun OnecID.to(id: MageID) = mapOnecIDToMageID.put(this.id, id.id)

    infix fun OnecID.to(path: MagePath) = mapOnecIDToMagePath.put(this.id, path.path)

    infix fun MagePath.to(id: OnecID) = mapMagePathToOnecID.put(this.path, id.id)

    infix fun MagePath.to(path: OnecPath) = mapMagePathToOnecPath.put(this.path, path.path)

    infix fun OnecPath.to(id: MageID) = mapOnecPathToMageID.put(this.path, id.id)

    infix fun OnecPath.to(path: MagePath) = mapOnecPathToMagePath.put(this.path, path.path)

    data class MageID(val id: Any)
    data class MagePath(val path: String)
    data class OnecID(val id: Any)
    data class OnecPath(val path: String)

    fun build(): CategoryMappingConfiguration {
        return CategoryMappingConfiguration(
            mapMageIDToOnecID,
            mapMageIDToOnecPath,
            mapOnecIDToMageID,
            mapOnecIDToMagePath,
            mapMagePathToOnecPath,
            mapMagePathToOnecID,
            mapOnecPathToMagePath,
            mapOnecPathToMageID
        )
    }
}
