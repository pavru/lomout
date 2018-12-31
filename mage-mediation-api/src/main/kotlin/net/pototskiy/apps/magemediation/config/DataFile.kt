package net.pototskiy.apps.magemediation.config

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class DataFile {
    @field:XmlAttribute(required = true)
    var id: String = ""
    @field:XmlAttribute(required = true)
    var path: String = ""
}
