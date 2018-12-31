package net.pototskiy.apps.magemediation.config.dataset

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class AttrListDefinition {
    @XmlAttribute(name = "attr-delimiter")
    var attrDelimiter: String = ","
    @XmlAttribute(name = "attr-quote")
    var attrQuote: String = "\""
    @XmlAttribute(name = "name-value-delimiter")
    var nameValueDelimiter: String = "="
    @XmlAttribute(name = "value-quote")
    var valueQuote: String = "\""
}