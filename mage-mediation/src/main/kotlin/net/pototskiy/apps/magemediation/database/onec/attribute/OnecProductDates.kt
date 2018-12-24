package net.pototskiy.apps.magemediation.database.onec.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeTable
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntity
import net.pototskiy.apps.magemediation.database.attribute.TypedAttributeEntityClass
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime

object OnecProductDates : TypedAttributeTable<DateTime>("onec_product_date") {
    override val owner = reference("owner", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = date("value")
}

class OnecProductDate(id: EntityID<Int>) : TypedAttributeEntity<DateTime>(id) {
    companion object : TypedAttributeEntityClass<DateTime, OnecProductDate>(OnecProductDates)

    override var owner: VersionEntity by OnecProduct referencedOn OnecProductDates.owner
    override var index by OnecProductDates.index
    override var code by OnecProductDates.code
    override var value by OnecProductDates.value
}