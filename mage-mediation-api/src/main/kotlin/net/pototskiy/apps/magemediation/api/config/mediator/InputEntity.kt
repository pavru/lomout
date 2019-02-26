package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.entity.*
import net.pototskiy.apps.magemediation.api.plugable.SqlFilterPlugin
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.util.*

data class InputEntity(
    val entity: EType,
    val entityExtension: EType?,
    val filter: SqlFilter?,
    val extAttrMaps: AttrMapCollection
) {
    fun extendedAttributes(entity: DbEntity): Map<AnyTypeAttribute, Type?> {
        return extAttrMaps.keys.map { attr->
            @Suppress("UNCHECKED_CAST")
            attr to (attr.reader as AttributeReader<Type>)
                .read(attr,AttributeCell(extAttrMaps[attr] as Attribute<Type>,entity.data[extAttrMaps[attr]]))
        }.toMap()
    }

    @ConfigDsl
    class Builder(@ConfigDsl val eType: EType) {
        @ConfigDsl
        val attrPairs = mutableMapOf<Attribute<*>, Attribute<*>>()
        @ConfigDsl
        var sqlFilter: SqlFilter? = null
        private val extEntityUUID = UUID.randomUUID().toString()

        fun filter(block: SqlExpressionBuilder.(alias: Alias<DbEntityTable>) -> Op<Boolean>) {
            sqlFilter = SqlFilterWithFunction { alias: Alias<DbEntityTable> -> Op.build { block(alias) } }
        }

        inline fun <reified P : SqlFilterPlugin> filter(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            sqlFilter = SqlFilterWithPlugin(P::class, block as (SqlFilterPlugin.() -> Unit))
        }

        inline fun <reified T : Type> extAttribute(
            name: String,
            from: String,
            block: Attribute.Builder<T>.() -> Unit = {}
        ) {
            val destAttr = Attribute.Builder<T>(extendedName(eType.type), name, T::class).apply(block).build()
            val origData = EntityAttributeManager.getAttribute(AttributeName(eType.type, from))
                ?: throw ConfigException("Attribute<${AttributeName(eType.type, from)} is not defined>")
            attrPairs[destAttr] = origData
        }

        fun extendedName(type: String): String = "$type${"$$"}ext${"$$"}$extEntityUUID"

        fun build(): InputEntity {
            val extEntity = if (attrPairs.isEmpty()) {
                null
            } else {
                EntityTypeManager.createEntityType(
                    extendedName(eType.type),
                    emptyList(),
                    AttributeCollection(attrPairs.keys.toList()),
                    false
                )
            }
            return InputEntity(eType, extEntity, sqlFilter, AttrMapCollection(attrPairs))
        }
    }
}
