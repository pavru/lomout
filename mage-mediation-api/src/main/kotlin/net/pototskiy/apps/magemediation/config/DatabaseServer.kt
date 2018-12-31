package net.pototskiy.apps.magemediation.config

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class DatabaseServer {
    @field:XmlAttribute(required = true)
    var host = "localhost"
    @field:XmlAttribute
    var port: Int? = 3306
    @field:XmlAttribute(required = true)
    var user = "root"
    @field:XmlAttribute(required = true)
    var password = "root"
}
