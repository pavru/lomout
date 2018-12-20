package net.pototskiy.apps.magemediation.config

import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
class Database {
    @field:XmlElement(required = true)
    var server = DatabaseServer()
    @field:XmlAttribute(required = true)
    var name = "magemediation"
}
