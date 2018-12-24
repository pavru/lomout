package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductVarchars : TypedAttributeTable<String>("onec_product_varchar") {
    override val owner = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = varchar("value", 800)
}

class OnecProductVarchar(id: EntityID<Int>) : TypedAttributeEntity<String>(id) {
    companion object : TypedAttributeEntityClass<String, OnecProductVarchar>(OnecProductVarchars)

    override var owner: IntEntity by OnecProduct referencedOn OnecProductVarchars.owner
    override var index by OnecProductVarchars.index
    override var code by OnecProductVarchars.code
    override var value by OnecProductVarchars.value
}