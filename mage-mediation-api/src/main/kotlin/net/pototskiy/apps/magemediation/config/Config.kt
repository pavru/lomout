package net.pototskiy.apps.magemediation.config

import net.pototskiy.apps.magemediation.LOG_NAME
import net.pototskiy.apps.magemediation.config.dataset.Dataset
import net.pototskiy.apps.magemediation.config.mediator.MediatorConfiguration
import org.apache.log4j.Logger
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
class Config : ConfigValidate {
    var database = DatabaseConfig()
    @field:XmlElementWrapper(name = "files", required = true)
    @field:XmlElement(name = "file")
    var files: List<DataFile> = mutableListOf()
    @field:XmlElementWrapper(name = "data-sets")
    @field:XmlElement(name = "dataset")
    var datasets: List<Dataset> = mutableListOf()
    @field:XmlElement(name="mediator")
    var mediator: MediatorConfiguration = MediatorConfiguration()

    @Suppress("unused")
    @XmlTransient
    private val logger = Logger.getLogger(LOG_NAME)

    override fun validate(parent: Any?) {
        validateFileIdUnique()
        validateFileRefs()
    }

    private fun validateFileRefs() {
        val allowedFiles = files.map { it.id }
        val usedFiles = datasets.map { dataset -> dataset.sources.map { it.file } }.flatten()
        if (!allowedFiles.containsAll(usedFiles)) {
            val wrongRefs = usedFiles.minus(allowedFiles)
            throw ConfigException("Files<${wrongRefs.joinToString(", ")}> are not defined in files section")
        }
    }

    private fun validateFileIdUnique() {
        val ids = files.asSequence()
            .map { it.id }
            .groupBy { it }
            .filter { it.value.count() > 1 }
        if (ids.any { it.value.count() > 1 }) {
            throw ConfigException("File ids<${ids.keys.joinToString(", ")}> are duplicated")
        }
    }
}
