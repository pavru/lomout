package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.Config
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.config.data.Entity
import net.pototskiy.apps.magemediation.api.plugable.*
import kotlin.reflect.full.createInstance

data class ProcessorCollection(private val processors: List<EntityProcessor>) :
    List<EntityProcessor> by processors {
    fun getMatchedProcessor(): MatchedEntityProcessor? {
        return find { it is MatchedEntityProcessor } as? MatchedEntityProcessor
    }

    fun getUnMatchedProcessor(name: String): UnMatchedEntityProcessor? {
        return find {
            it is UnMatchedEntityProcessor && it.entityType.name == name
        } as? UnMatchedEntityProcessor
    }

    fun getUnMatchedProcessor(entity: Entity): UnMatchedEntityProcessor? {
        return find {
            it is UnMatchedEntityProcessor && it.entityType.name == entity.name
        } as? UnMatchedEntityProcessor
    }

    @ConfigDsl
    class Builder {
        @Suppress("PropertyName")
        var __matched: MatchedEntityProcessor? = null
        @Suppress("PropertyName")
        var __unmatched = mutableListOf<UnMatchedEntityProcessor>()

        @Suppress("unused")
        inline fun <reified P : MatchedEntityProcessorPlugin> Builder.matched() {
            __matched = MatchedEntityProcessorWithPlugin(P::class)
        }

        @Suppress("unused")
        @JvmName("matched__plugin_option")
        inline fun <reified P : MatchedEntityProcessorPlugin, O : NewPlugin.Options> Builder.matched(block: O.() -> Unit) {
            val plugin = P::class.createInstance()
            @Suppress("UNCHECKED_CAST")
            val options = (plugin.optionSetter() as O).apply(block)
            __matched = MatchedEntityProcessorWithPlugin(P::class, options)
        }

        @Suppress("unused")
        @JvmName("matched__function")
        fun Builder.matched(block: MatchedEntityProcessorFunction) {
            __matched = MatchedEntityProcessorWithFunction(block)
        }

        @Suppress("unused")
        inline fun <reified P : UnMatchedEntityProcessorPlugin> Builder.unmatched(name: String) {
            val entity = Config.Builder.definedEntities.findRegistered(name, true)
                ?: throw ConfigException("Entity<$name> is not defines")
            __unmatched.add(UnMatchedEntityProcessorWithPlugin(entity, P::class))
        }

        @Suppress("unused")
        @JvmName("unmatched__entity_name__plugin_options")
        inline fun <reified P : UnMatchedEntityProcessorPlugin, O : NewPlugin.Options> Builder.unmatched(
            name: String,
            block: O.() -> Unit
        ) {
            val entity = Config.Builder.definedEntities.findRegistered(name, true)
                ?: throw ConfigException("Entity<$name> is not defined")
            val plugin = P::class.createInstance()
            @Suppress("UNCHECKED_CAST")
            val options = (plugin.optionSetter() as O).apply(block)
            __unmatched.add(UnMatchedEntityProcessorWithPlugin(entity, P::class, options))
        }

        @Suppress("unused")
        @JvmName("unmatched__entity_name__processor_function")
        fun Builder.unmatched(name: String, block: UnMatchedEntityProcessorFunction) {
            val entity = Config.Builder.definedEntities.findRegistered(name, true)
                ?: throw ConfigException("Entity<$name> is not defined")
            __unmatched.add(UnMatchedEntityProcessorWithFunction(entity, block))
        }

        fun build(): ProcessorCollection {
            val processors = if (__matched == null) __unmatched else __unmatched.plus(__matched!!)
            return ProcessorCollection(processors)
        }
    }
}
