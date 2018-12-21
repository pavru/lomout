package net.pototskiy.apps.magemediation.config

import javax.xml.bind.annotation.XmlEnum
import javax.xml.bind.annotation.XmlEnumValue
import javax.xml.bind.annotation.XmlType

@XmlType
@XmlEnum(String::class)
enum class DatasetTarget {
    @field:XmlEnumValue("onec-product")
    ONEC_PRODUCT,
    @field:XmlEnumValue("onec-group")
    ONEC_GROUP,
    @field:XmlEnumValue("mage-product")
    MAGE_PRODUCT,
    @field:XmlEnumValue("mage-category")
    MAGE_CATEGORY,
    @field:XmlEnumValue("mage-price")
    MAGE_PRICE,
    @field:XmlEnumValue("mage-inventory")
    MAGE_INVENTORY,
    @field:XmlEnumValue("mage-user-group")
    MAGE_USER_GROUP
}
