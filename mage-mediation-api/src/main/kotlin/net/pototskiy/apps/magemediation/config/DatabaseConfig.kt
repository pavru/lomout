package net.pototskiy.apps.magemediation.config

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.FIELD)
class DatabaseConfig {
    @field:XmlElement(required = true)
    var server = DatabaseServer()
    @field:XmlAttribute(required = true)
    var name = "magemediation"
}
