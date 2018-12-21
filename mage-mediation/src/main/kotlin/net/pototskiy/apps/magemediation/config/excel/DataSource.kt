package net.pototskiy.apps.magemediation.config.excel

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class DataSource {
    @field:XmlAttribute(required = true)
    var file: String = ""
    @field:XmlAttribute(required = true)
    var sheet: String = ""
}
