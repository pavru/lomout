package net.pototskiy.apps.magemediation.config.excel

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class ListDefinition {
    @field:XmlAttribute(required = true)
    var delimeter: String = ","
    @field:XmlAttribute(required = true)
    var quote: String = ""
}
