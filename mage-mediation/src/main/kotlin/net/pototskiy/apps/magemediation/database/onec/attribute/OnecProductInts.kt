package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductInts : TypedAttributeTable<Long>("onec_product_int") {
    override val owner = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = long("value")
}

class OnecProductInt(id: EntityID<Int>) : TypedAttributeEntity<Long>(id) {
    companion object : TypedAttributeEntityClass<Long, OnecProductInt>(OnecProductInts)

    override var owner: IntEntity by OnecProduct referencedOn OnecProductInts.owner
    override var index by OnecProductInts.index
    override var code by OnecProductInts.code
    override var value by OnecProductInts.value
}