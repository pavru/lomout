package net.pototskiy.apps.magemediation.config.excel

import net.pototskiy.apps.magemediation.config.DataFile

import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
class DataSource {
    @field:XmlAttribute(required = true)
    var file: String = ""
    @field:XmlAttribute(required = true)
    var sheet: String = ""
}
