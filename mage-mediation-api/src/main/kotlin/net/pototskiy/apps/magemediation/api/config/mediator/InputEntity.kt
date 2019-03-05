package net.pototskiy.apps.magemediation.api.config.mediator

import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import net.pototskiy.apps.magemediation.api.database.DbEntity
import net.pototskiy.apps.magemediation.api.database.DbEntityTable
import net.pototskiy.apps.magemediation.api.entity.AnyTypeAttribute
import net.pototskiy.apps.magemediation.api.entity.Attribute
import net.pototskiy.apps.magemediation.api.entity.AttributeCell
import net.pototskiy.apps.magemediation.api.entity.AttributeCollection
import net.pototskiy.apps.magemediation.api.entity.AttributeName
import net.pototskiy.apps.magemediation.api.entity.AttributeReader
import net.pototskiy.apps.magemediation.api.entity.EntityType
import net.pototskiy.apps.magemediation.api.entity.EntityAttributeManager
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.Type
import net.pototskiy.apps.magemediation.api.plugable.SqlFilterPlugin
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.util.*
import kotlin.collections.Map
import kotlin.collections.emptyList
import kotlin.collections.get
import kotlin.collections.map
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toList
import kotlin.collections.toMap

data class InputEntity(
    val entity: EntityType,
    val entityExtension: EntityType?,
    val filter: SqlFilter?,
    val extAttrMaps: AttrMapCollection
) {
    fun extendedAttributes(entity: DbEntity): Map<AnyTypeAttribute, Type?> {
        return extAttrMaps.keys.map { attr ->
            @Suppress("UNCHECKED_CAST")
            attr to (attr.reader as AttributeReader<Type>)
                .read(attr, AttributeCell(extAttrMaps[attr] as Attribute<Type>, entity.data[extAttrMaps[attr]]))
        }.toMap()
    }

    @ConfigDsl
    class Builder(@ConfigDsl val entityType: EntityType) {
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
            val destAttr = Attribute.Builder<T>(extendedName(entityType.name), name, T::class).apply(block).build()
            val origData = EntityAttributeManager.getAttributeOrNull(AttributeName(entityType.name, from))
                ?: throw ConfigException("Attribute<${AttributeName(entityType.name, from)} is not defined>")
            attrPairs[destAttr] = origData
        }

        fun extendedName(type: String): String = "$type${"$$"}ext${"$$"}$extEntityUUID"

        fun build(): InputEntity {
            val extEntity = if (attrPairs.isEmpty()) {
                null
            } else {
                EntityTypeManager.createEntityType(
                    extendedName(entityType.name),
                    emptyList(),
                    AttributeCollection(attrPairs.keys.toList()),
                    false
                )
            }
            return InputEntity(entityType, extEntity, sqlFilter, AttrMapCollection(attrPairs))
        }
    }
}
