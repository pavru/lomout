package net.pototskiy.apps.lomout.api

import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import org.bson.conversions.Bson
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.and
import org.litote.kmongo.eq

/**
 * Create Bson filter from the map of attribute. Logical and is used for concatenation.
 *
 * @receiver Map<Attribute, Any>
 * @return Bson
 */
@Suppress("SpreadOperator")
fun Map<Attribute, Any>.toFilter(): Bson {
    val bson = this.map { it.key.property eq it.value }.toTypedArray()
    return when {
        bson.isEmpty() -> EMPTY_BSON
        bson.size == 1 -> bson[0]
        else -> and(*bson)
    }
}
