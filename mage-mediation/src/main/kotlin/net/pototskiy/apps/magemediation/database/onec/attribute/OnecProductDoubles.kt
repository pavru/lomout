package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.database.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductDoubles : TypedAttributeTable<Double>("onec_product_double") {
    override val owner = reference("owner", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = double("value")
}

class OnecProductDouble(id: EntityID<Int>) : TypedAttributeEntity<Double>(id) {
    companion object : TypedAttributeEntityClass<Double, OnecProductDouble>(OnecProductDoubles)

    override var owner: IntEntity by OnecProduct referencedOn OnecProductDoubles.owner
    override var index by OnecProductDoubles.index
    override var code by OnecProductDoubles.code
    override var value by OnecProductDoubles.value
}