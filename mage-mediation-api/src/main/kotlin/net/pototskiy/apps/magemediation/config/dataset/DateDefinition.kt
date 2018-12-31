package net.pototskiy.apps.magemediation.config.dataset

import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.ConfigValidate
import org.joda.time.format.DateTimeFormat
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class DateDefinition : ConfigValidate {
    @field:XmlAttribute
    var format: String? = null

    override fun validate(parent: Any?) {
        try {
            DateTimeFormat.forPattern(format)
        } catch (e: IllegalArgumentException) {
            throw ConfigException("Pattern<$format> is not right date template")
        }
    }
}
