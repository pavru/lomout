package net.pototskiy.apps.magemediation.config.mediator

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.FIELD)
class OnecMediatorConfiguration {
    @field:XmlElement(name = "group", required = false)
    var groupConfiguration: OnecGroupMediatorConfiguration? = null
}