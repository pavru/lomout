package net.pototskiy.apps.magemediation.config

import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
class DataFile {
    @field:XmlAttribute(required = true)
    var id: String = ""
    @field:XmlAttribute(required = true)
    var path: String = ""
}
