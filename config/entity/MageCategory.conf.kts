@file:Import("../builder/CategoryPathFromRelation.plugin.conf.kts")

import CategoryPathFromRelation_plugin_conf.CategoryPathFromRelation
import org.bson.codecs.pojo.annotations.BsonIgnore

open class MageCategory : Document() {
    var entity_type_id: Long? = null
    var name: String = ""
    var attribute_set_id: Long = 0L
    class DateTimeReaderBuilder: ReaderBuilder{
        override fun build(): AttributeReader<out Any?> = createReader<DateTimeAttributeReader> {
            pattern = "u-M-d H:m:s"
        }
    }
    @Reader(DateTimeReaderBuilder::class)
    var created_at: LocalDateTime = LocalDateTime.now()
    @Reader(DateTimeReaderBuilder::class)
    var updated_at: LocalDateTime = LocalDateTime.now()
    var parent_id: Long = 0L
    var increment_id: Long? = null
    @Key
    var entity_id: Long = -1L
    var children: String? = null
    var children_count: Long = 0L
    var description: String? = null
    var include_in_menu: Boolean = false
    var is_active: Boolean? = null
    var is_anchor: Boolean? = null
    var is_virtual_category: Boolean? = null
    var level: Long = 0L
    var position: Long = 0L
    var use_name_in_product_search: Boolean = false
    var gen_store_id: Long = 0L
    class GenProductsReader : ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<LongListAttributeReader> {
            delimiter = '|'; quote = null
        }
    }
    @Reader(GenProductsReader::class)
    var gen_products: List<Long>? = null
    @get:BsonIgnore
    val __path: String by lazy { pathBuilder.build(this)!! }

    companion object : DocumentMetadata(MageCategory::class) {
        val pathBuilder = CategoryPathFromRelation("/","/")
    }
}