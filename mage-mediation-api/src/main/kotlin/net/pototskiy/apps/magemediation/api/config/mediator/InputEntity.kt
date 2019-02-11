package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.data.*
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformFunction
import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformPlugin
import kotlin.reflect.full.createInstance

data class InputEntity(
    val entity: Entity,
    val attrMaps: AttrMapCollection
) {
    fun mapAttributes(entity: PersistentSourceEntity): Map<Attribute, Any?> {
        return entity.data.map { (oldAttr, oldValue) ->
            attrMaps[oldAttr]?.let { attrMap ->
                attrMap.transformer?.let {
                    attrMap.attribute to it.transform(oldValue)
                } ?: attrMap.attribute to oldValue
            } ?: (oldAttr to oldValue)
        }.toMap()
    }

    @ConfigDsl
    class Builder(private val entity: Entity) {
        private val attrPairs = mutableListOf<Pair<Attribute, Attribute>>()
        @Suppress("PropertyName")
        val __attrTransformers = mutableMapOf<Pair<Attribute, Attribute>, NewTransformer<Any?, Any?>>()

        @Suppress("unused")
        fun Builder.attribute(name: String, block: Attribute.Builder.() -> Unit = {}): Attribute {
            return Attribute.Builder(name).apply(block).build()
        }

        infix fun Attribute.to(attr: Attribute): Pair<Attribute, Attribute> {
            val pair = Pair(this, attr)
            attrPairs.add(pair)
            return pair
        }

        inline fun <reified P : NewValueTransformPlugin<T, R>, T : Any?, R : Any?> Pair<Attribute, Attribute>.withTransform() {
            @Suppress("UNCHECKED_CAST")
            __attrTransformers[this] = NewTransformerWithPlugin(P::class) as NewTransformer<Any?, Any?>
        }

        @JvmName("with_transform__plugin__options")
        inline fun <reified P : NewValueTransformPlugin<T, R>, O : NewPlugin.Options, T : Any?, R : Any?> Pair<Attribute, Attribute>.withTransform(
            block: O.() -> Unit
        ) {
            val plugin = P::class.createInstance()
            @Suppress("UNCHECKED_CAST")
            val options = (plugin.optionSetter() as O).apply(block)
            @Suppress("UNCHECKED_CAST")
            __attrTransformers[this] = NewTransformerWithPlugin(P::class, options) as NewTransformer<Any?, Any?>
        }

        @JvmName("with_transform__function")
        fun <T : Any?, R : Any?> Pair<Attribute, Attribute>.withTransform(block: NewValueTransformFunction<T, R>) {
            @Suppress("UNCHECKED_CAST")
            __attrTransformers[this] = NewTransformerWithFunction(block) as NewTransformer<Any?, Any?>
        }

        fun build(): InputEntity {
            val attrMaps = mutableMapOf<Attribute, AttrMap>()
            attrPairs.forEach {
                val valueTransformer = __attrTransformers[it]
                attrMaps[it.first] = AttrMap(it.second, valueTransformer)
            }
            return InputEntity(entity, AttrMapCollection(attrMaps))
        }
    }
}
