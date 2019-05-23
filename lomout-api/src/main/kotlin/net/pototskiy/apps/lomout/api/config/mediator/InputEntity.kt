package net.pototskiy.apps.lomout.api.config.mediator

import net.pototskiy.apps.lomout.api.AppAttributeException
import net.pototskiy.apps.lomout.api.config.ConfigBuildHelper
import net.pototskiy.apps.lomout.api.config.ConfigDsl
import net.pototskiy.apps.lomout.api.database.DbEntity
import net.pototskiy.apps.lomout.api.database.DbEntityTable
import net.pototskiy.apps.lomout.api.entity.AnyTypeAttribute
import net.pototskiy.apps.lomout.api.entity.Attribute
import net.pototskiy.apps.lomout.api.entity.AttributeAsCell
import net.pototskiy.apps.lomout.api.entity.AttributeCollection
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.EntityType
import net.pototskiy.apps.lomout.api.entity.Type
import net.pototskiy.apps.lomout.api.plugable.SqlFilterPlugin
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.util.*
import kotlin.collections.set

/**
 * Pipeline input entity
 *
 * @property entity EntityType The entity type
 * @property entityExtension EntityType? The input extension entity type
 * @property filter SqlFilter? The SQL filter to select entities
 * @property extAttrMaps AttrMapCollection The map of extended attribute
 * @constructor
 */
data class InputEntity(
    val entity: EntityType,
    val entityExtension: EntityType?,
    val filter: SqlFilter?,
    val extAttrMaps: AttrMapCollection
) {
    /**
     * Get all extended attributes
     *
     * @param entity DbEntity The DB entity
     * @return Map<AnyTypeAttribute, Type?>
     */
    fun extendedAttributes(entity: DbEntity): Map<AnyTypeAttribute, Type?> {
        return extAttrMaps.keys.map { attr ->
            @Suppress("UNCHECKED_CAST")
            attr to (attr.reader as AttributeReader<Type>)
                .read(attr, AttributeAsCell(extAttrMaps[attr] as Attribute<Type>, entity.data[extAttrMaps[attr]]))
        }.toMap()
    }

    /**
     * Pipeline input entity definition builder class
     *
     * @property helper ConfigBuildHelper The config build helper
     * @property entityType EntityType The base entity type
     * @property attrPairs MutableMap<Attribute<*>, Attribute<*>> The map of extended attribute
     * @property sqlFilter SqlFilter? The SQL filter to select entities
     * @property extEntityUUID String The unique id to generate extended entity type name
     * @constructor
     */
    @ConfigDsl
    class Builder(
        val helper: ConfigBuildHelper,
        val entityType: EntityType
    ) {
        val attrPairs = mutableMapOf<Attribute<*>, Attribute<*>>()
        var sqlFilter: SqlFilter? = null
        private val extEntityUUID = UUID.randomUUID().toString()

        /**
         * Inline definition of SQL filter to select entities
         *
         * ```
         * ...
         *  filter {
         *      it[DbEntityTable.currentStatus] new EntityStatus.REMOVED
         *  }
         * ...
         * ```
         *
         * @param block The filter code
         */
        @ConfigDsl
        fun filter(block: SqlExpressionBuilder.(alias: Alias<DbEntityTable>) -> Op<Boolean>) {
            sqlFilter = SqlFilterWithFunction { alias: Alias<DbEntityTable> -> Op.build { block(alias) } }
        }

        /**
         * Define SQL filter with a plugin
         *
         * ```
         * ...
         *  filter<FilterPluginClass>()
         * ...
         * ```
         * [FilterPluginClass][net.pototskiy.apps.lomout.api.plugable.SqlFilterPlugin] — filter plugin class,
         *      **mandatory**
         *
         * @param block The filter options
         */
        @ConfigDsl
        inline fun <reified P : SqlFilterPlugin> filter(noinline block: P.() -> Unit = {}) {
            @Suppress("UNCHECKED_CAST")
            sqlFilter = SqlFilterWithPlugin(P::class, block as (SqlFilterPlugin.() -> Unit))
        }

        /**
         * Define an extended attribute of input entity
         *
         * ```
         * ...
         *  extAttribute<Type>("ext name", "base name") {
         *      reader {...}
         *      writer {...}
         *  }
         * ...
         * ```
         *
         * @param T The extended attribute type
         * @param name The name of extended attribute
         * @param from The name of base attribute
         * @param block The extended attribute definition
         */
        @ConfigDsl
        inline fun <reified T : Type> extAttribute(
            name: String,
            from: String,
            block: Attribute.Builder<T>.() -> Unit = {}
        ) {
            val destAttr = Attribute.Builder(helper, name, T::class).apply(block).build()
            val origData = this.helper.typeManager.getEntityAttribute(entityType, from)
                ?: throw AppAttributeException("Attribute<${entityType.name}:$from> is not defined>")
            attrPairs[origData] = destAttr
        }

        private fun extendedName(type: String): String = "$type${"$$"}ext${"$$"}$extEntityUUID"

        /**
         * Build input entity definition
         *
         * @return InputEntity
         */
        fun build(): InputEntity {
            val extEntity = if (attrPairs.isEmpty()) {
                null
            } else {
                helper.typeManager.createEntityType(
                    extendedName(entityType.name),
                    emptyList(),
                    false
                ).also { helper.typeManager.initialAttributeSetup(it, AttributeCollection(attrPairs.values.toList())) }
            }
            return InputEntity(
                entityType,
                extEntity,
                sqlFilter,
                AttrMapCollection(attrPairs.map { it.value to it.key }.toMap())
            )
        }
    }
}
