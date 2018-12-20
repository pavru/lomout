package net.pototskiy.apps.magemediation.database.attribute

import net.pototskiy.apps.magemediation.database.VersionEntity
import net.pototskiy.apps.magemediation.database.onec.OnecProduct
import net.pototskiy.apps.magemediation.database.onec.OnecProducts
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object OnecProductTexts : TextAttribute("onec_product_text") {
    override val product = reference("product", OnecProducts, onDelete = ReferenceOption.CASCADE)
    override val value = text("value")
}

class OnecProductText(id: EntityID<Int>) : TextAttributeEntity(id) {
    companion object : TextAttributeEntityClass<OnecProductText>(OnecProductTexts)

    override var product:VersionEntity by OnecProduct referencedOn OnecProductTexts.product
    override var index by OnecProductTexts.index
    override var code by OnecProductTexts.code
    override var value by OnecProductTexts.value
}