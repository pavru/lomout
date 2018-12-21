package net.pototskiy.apps.magemediation.config.excel

import net.pototskiy.apps.magemediation.LOG_NAME
import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.ConfigValidate
import net.pototskiy.apps.magemediation.config.excel.Field.Companion.UNDEFINED_COLUMN
import org.apache.log4j.Logger
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
open class FieldSet : ConfigValidate {
    @field:XmlAttribute(required = true)
    var name: String = ""
    @XmlTransient
    var type: FieldSetType = FieldSetType.MAIN
    @field:XmlElement(name = "field", required = true)
    var fields: List<Field> = mutableListOf()

    @Suppress("unused")
    @XmlTransient
    private val logger = Logger.getLogger(LOG_NAME)

    override fun validate(parent: Any?) {
        if (type == FieldSetType.MAIN && !fields.any { it.keyField }) {
            throw ConfigException("Field set<$name> has type MAIN but does not have any key field")
        }
        validateDuplicatedColumns()
        validateNestedParents()
        validateRecursiveRef()
    }

    private fun validateNestedParents() {
        val fieldNames = fields.map { it.name }
        val nestedFields = fields.filter { it.nested }.map { it.parent }
        if (!fieldNames.containsAll(nestedFields)) {
            val hasWrongParent = fields.asSequence()
                .filter { it.nested }
                .filter { !fieldNames.contains(it.parent) }
                .map { it.name }
            throw ConfigException("Parents of fields<${hasWrongParent.joinToString(", ")}> do not exist")
        }
    }

    private fun validateDuplicatedColumns() {
        val duplicatedColumn = fields.asSequence()
            .filterNot { it.column == UNDEFINED_COLUMN }
            .groupBy { it.column }
            .filter { it.value.count() > 1 }
        if (duplicatedColumn.any { it.value.count() > 1 }) {
            val fields = duplicatedColumn
                .map { it.value }
                .flatten()
                .map { it.name }
                .toList()
            throw ConfigException("Fields<${fields.joinToString(", ")}> have the same column>")
        }
    }

    private fun validateRecursiveRef() {
        for (f in fields.filter { it.nested }) {
            val visited = mutableListOf<Field>()
            visited.add(f)
            var v = f
            do {
                v = fields.find { it.name == v.parent }!!
                if (visited.any { it.name == v.name }) {
                    throw ConfigException("Filed<${f.name}> has recursion in nested chain")
                }
                visited.add(v)
            } while (v.nested)

        }
    }
}
