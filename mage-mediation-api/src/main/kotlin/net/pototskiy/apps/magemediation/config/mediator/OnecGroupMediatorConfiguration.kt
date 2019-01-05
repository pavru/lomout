package net.pototskiy.apps.magemediation.config.mediator

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class OnecGroupMediatorConfiguration {
    @field:XmlAttribute(name = "structure", required = true)
    var groupCodeStructure: String = ".*"
    @field:XmlAttribute(name = "sub-group-filler", required = true)
    var subGroupFiller: String = "0"
    @field:XmlAttribute(name = "separator", required = false)
    var separator: String = "/"
    @field:XmlAttribute(name = "root", required = false)
    var root: String = ""
}