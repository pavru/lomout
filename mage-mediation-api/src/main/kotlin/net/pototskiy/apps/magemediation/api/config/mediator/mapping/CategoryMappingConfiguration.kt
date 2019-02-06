package net.pototskiy.apps.magemediation.api.config.mediator.mapping

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.data.Transformer
import net.pototskiy.apps.magemediation.api.config.data.TransformerFunction
import net.pototskiy.apps.magemediation.api.config.data.TransformerPlugin
import net.pototskiy.apps.magemediation.api.config.type.Attribute
import net.pototskiy.apps.magemediation.api.plugable.ValueTransformFunction
import net.pototskiy.apps.magemediation.api.plugable.ValueTransformPlugin
import kotlin.reflect.KClass

class CategoryMappingConfiguration(
    val mageIDToOnecID: Map<Any, Any>,
    val mageIDtoOnecPath: Map<Any, String>,
    val onecIDToMageID: Map<Any, Any>,
    val onecIDToMagePath: Map<Any, String>,
    val magePathToOnecPath: Map<String, String>,
    val magePathToOnecID: Map<String, Any>,
    val onecPathToMagePath: Map<String, String>,
    val onecPathToMageID: Map<String, Any>,
    val mageAttrToOnecAttr: Map<Attribute, AttributeWithTransformation>,
    val onecAttrToMageAttr: Map<Attribute, AttributeWithTransformation>
) {
    class Builder {
        private val mapMageIDToOnecID = mutableMapOf<Any, Any>()
        private val mapMageIDToOnecPath = mutableMapOf<Any, String>()
        private val mapOnecIDToMageID = mutableMapOf<Any, Any>()
        private val mapOnecIDToMagePath = mutableMapOf<Any, String>()
        private val mapMagePathToOnecID = mutableMapOf<String, Any>()
        private val mapMagePathToOnecPath = mutableMapOf<String, String>()
        private val mapOnecPathToMageID = mutableMapOf<String, Any>()
        private val mapOnecPathToMagePath = mutableMapOf<String, String>()
        private val mapMageAttrToOnecAttr = mutableMapOf<Attribute, AttributeWithTransformation>()
        private val mapOnecAttrToMageAttr = mutableMapOf<Attribute, AttributeWithTransformation>()

        @Suppress("unused")
        fun Builder.mageID(id: String) = MageID(id)

        @Suppress("unused")
        fun Builder.mageID(id: Long) = MageID(id)

        @Suppress("unused")
        fun Builder.onecID(id: String) = OnecID(id)

        @Suppress("unused")
        fun Builder.onecID(id: Long) = OnecID(id)

        @Suppress("unused")
        fun Builder.magePath(path: String) = MagePath(path)

        @Suppress("unused")
        fun Builder.onecPath(path: String) = OnecPath(path)

        infix fun MageID.to(id: OnecID) = mapMageIDToOnecID.put(this.id, id.id)

        infix fun MageID.to(path: OnecPath) = mapMageIDToOnecPath.put(this.id, path.path)

        infix fun OnecID.to(id: MageID) = mapOnecIDToMageID.put(this.id, id.id)

        infix fun OnecID.to(path: MagePath) = mapOnecIDToMagePath.put(this.id, path.path)

        infix fun MagePath.to(id: OnecID) = mapMagePathToOnecID.put(this.path, id.id)

        infix fun MagePath.to(path: OnecPath) = mapMagePathToOnecPath.put(this.path, path.path)

        infix fun OnecPath.to(id: MageID) = mapOnecPathToMageID.put(this.path, id.id)

        infix fun OnecPath.to(path: MagePath) = mapOnecPathToMagePath.put(this.path, path.path)

        @Suppress("unused")
        fun Builder.mageAttribute(
            name: String,
            block: Attribute.Builder.() -> Unit = {}
        ): MageAttribute = MageAttribute(Attribute.Builder(name).apply(block).build())

        @Suppress("unused")
        fun Builder.onecAttribute(
            name: String,
            block: Attribute.Builder.() -> Unit = {}
        ): OnecAttribute = OnecAttribute(Attribute.Builder(name).apply(block).build())

        fun <T : Any, R : Any> MageAttribute.transformTo(
            attribute: OnecAttribute,
            plugin: ValueTransformPlugin<T, R>? = null
        ) {
            @Suppress("UNCHECKED_CAST")
            mapMageAttrToOnecAttr[this.attribute] = AttributeWithTransformation(
                attribute.attribute,
                if (plugin == null) null else TransformerPlugin(plugin) as Transformer<Any, Any>
            )
        }

        fun <T : Any, R : Any> MageAttribute.transformTo(
            attribute: OnecAttribute,
            block: ValueTransformFunction<T, R>? = null
        ) {
            @Suppress("UNCHECKED_CAST")
            mapMageAttrToOnecAttr[this.attribute] = AttributeWithTransformation(
                attribute.attribute,
                if (block == null) null else TransformerFunction(block) as Transformer<Any, Any>
            )
        }

        @Suppress("unused")
        fun <T : Any, R : Any> OnecAttribute.transformTo(
            attribute: MageAttribute,
            plugin: ValueTransformPlugin<T, R>? = null
        ) {
            @Suppress("UNCHECKED_CAST")
            mapOnecAttrToMageAttr[this.attribute] = AttributeWithTransformation(
                attribute.attribute,
                if (plugin == null) null else TransformerPlugin(plugin) as Transformer<Any, Any>
            )
        }

        @Suppress("unused")
        fun <T : Any, R : Any> OnecAttribute.transformTo(
            attribute: MageAttribute,
            block: ValueTransformFunction<T, R>? = null
        ) {
            @Suppress("UNCHECKED_CAST")
            mapOnecAttrToMageAttr[this.attribute] = AttributeWithTransformation(
                attribute.attribute,
                if (block == null) null else TransformerFunction(block) as Transformer<Any, Any>
            )
        }

        @ConfigDsl
        class TransformBuilder<T : Any, R : Any> {
            private var klass: KClass<out ValueTransformPlugin<T, R>>? = null

            @Suppress("unused")
            fun <T : Any, R : Any> TransformBuilder<T, R>.klass(klass: KClass<out ValueTransformPlugin<T, R>>) {
                this.klass = klass
            }

            fun build(): KClass<out ValueTransformPlugin<T, R>>? {
                return klass
            }
        }

        data class MageID(val id: Any)
        data class MagePath(val path: String)
        data class OnecID(val id: Any)
        data class OnecPath(val path: String)
        data class MageAttribute(val attribute: Attribute)
        data class OnecAttribute(val attribute: Attribute)

        fun build(): CategoryMappingConfiguration {
            return CategoryMappingConfiguration(
                mapMageIDToOnecID,
                mapMageIDToOnecPath,
                mapOnecIDToMageID,
                mapOnecIDToMagePath,
                mapMagePathToOnecPath,
                mapMagePathToOnecID,
                mapOnecPathToMagePath,
                mapOnecPathToMageID,
                mapMageAttrToOnecAttr,
                mapOnecAttrToMageAttr
            )
        }
    }
}
