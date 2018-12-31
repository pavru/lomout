package net.pototskiy.apps.magemediation.config.dataset

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class DataSource {
    @field:XmlAttribute(required = true)
    var file: String = ""
    @field:XmlAttribute(required = true)
    var sheet: String = ""
    @field:XmlAttribute(name = "empty-row")
    var emptyRowAction: EmptyRowAction =
        EmptyRowAction.IGNORE
}
