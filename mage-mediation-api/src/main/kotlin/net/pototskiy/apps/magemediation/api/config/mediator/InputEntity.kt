package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.data.*
import net.pototskiy.apps.magemediation.api.database.PersistentSourceEntity
import net.pototskiy.apps.magemediation.api.database.schema.SourceEntities
import net.pototskiy.apps.magemediation.api.plugable.NewPlugin
import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformFunction
import net.pototskiy.apps.magemediation.api.plugable.NewValueTransformPlugin
import net.pototskiy.apps.magemediation.api.plugable.SqlFilterPlugin
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import kotlin.reflect.full.createInstance

data class InputEntity(
    val entity: Entity,
    val filter: SqlFilter?,
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
        @Suppress("PropertyName")
        var __sqlFilter: SqlFilter? = null

        @Suppress("unused")
        @JvmName("filter__function")
        fun Builder.filter(block: SqlExpressionBuilder.(alias: Alias<SourceEntities>) -> Op<Boolean>) {
            __sqlFilter = SqlFilterWithFunction { alias: Alias<SourceEntities> -> Op.build { block(alias) } }
        }

        @Suppress("unused")
        @JvmName("filter__plugin")
        inline fun <reified P : SqlFilterPlugin> filter() {
            __sqlFilter = SqlFilterWithPlugin(P::class)
        }

        @Suppress("unused")
        @JvmName("filter__plugin__options")
        inline fun <reified P : SqlFilterPlugin, O : NewPlugin.Options> Builder.filter(block: O.() -> Unit) {
            @Suppress("UNCHECKED_CAST")
            val options = (P::class.createInstance().optionSetter() as O).apply(block)
            __sqlFilter = SqlFilterWithPlugin(P::class, options)
        }

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

        @Suppress("unused")
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

        @Suppress("unused")
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
            return InputEntity(entity, __sqlFilter, AttrMapCollection(attrMaps))
        }
    }
}
