package net.pototskiy.apps.magemediation.config.dsl.mediator.onec

import net.pototskiy.apps.magemediation.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.dsl.type.AttributeTypeBuilder
import net.pototskiy.apps.magemediation.config.mediator.onec.OnecGroupMediatorConfiguration
import net.pototskiy.apps.magemediation.config.type.AttributeStringType
import net.pototskiy.apps.magemediation.config.type.AttributeTextType
import net.pototskiy.apps.magemediation.config.type.AttributeType

@ConfigDsl
class OnecGroupMediatorConfigurationBuilder {
    private var groupCodeAttributeName: String? = null
    private var groupCodeAttributeType: AttributeType? = null
    private var codeStructure: String = ".*"
    private var subCodeFiller: String = "0"
    private var pathSeparator: String = "/"
    private var pathRoot: String = ""

    @Suppress("unused")
    fun OnecGroupMediatorConfigurationBuilder.groupCodeAttribute(
        attribute: String,
        block: GroupCodeBuilder.() -> Unit
    ) {
        this.groupCodeAttributeName = attribute
        GroupCodeBuilder().apply(block)
    }


    fun build(): OnecGroupMediatorConfiguration {
        return OnecGroupMediatorConfiguration(
            groupCodeAttributeName ?: throw ConfigException("OneC group code attribute name must be configured"),
            groupCodeAttributeType ?: AttributeStringType(
                false,
                DEFAULT_LOCALE
            ),
            codeStructure,
            subCodeFiller,
            pathSeparator,
            pathRoot
        )
    }

    @ConfigDsl
    inner class GroupCodeBuilder {
        @Suppress("unused")
        fun GroupCodeBuilder.structure(structure: String) {
            this@OnecGroupMediatorConfigurationBuilder.codeStructure = structure
        }

        @Suppress("unused")
        fun GroupCodeBuilder.subCodeFiller(filler: String) {
            this@OnecGroupMediatorConfigurationBuilder.subCodeFiller = filler
        }

        @Suppress("unused")
        fun GroupCodeBuilder.separator(separator: String) {
            this@OnecGroupMediatorConfigurationBuilder.pathSeparator = separator
        }

        @Suppress("unused")
        fun GroupCodeBuilder.root(root: String) {
            this@OnecGroupMediatorConfigurationBuilder.pathRoot = root
        }

        @Suppress("unused")
        fun GroupCodeBuilder.type(block: AttributeTypeBuilder.()->Unit) {
            this@OnecGroupMediatorConfigurationBuilder.groupCodeAttributeType =
                    AttributeTypeBuilder(listOf(AttributeStringType::class, AttributeTextType::class)).apply(block).build()
        }
    }
}
