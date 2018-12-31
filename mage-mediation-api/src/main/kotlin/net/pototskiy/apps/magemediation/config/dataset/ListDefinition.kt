package net.pototskiy.apps.magemediation.config.dataset

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class ListDefinition {
    @field:XmlAttribute(required = true)
    var delimiter: String = ","
    @field:XmlAttribute(required = true)
    var quote: String = ""
}
