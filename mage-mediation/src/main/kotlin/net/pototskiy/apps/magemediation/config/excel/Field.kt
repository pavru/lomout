package net.pototskiy.apps.magemediation.config.excel

import net.pototskiy.apps.magemediation.LOG_NAME
import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.ConfigValidate
import org.slf4j.LoggerFactory
import java.util.*
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
class Field : ConfigValidate {

    @field:XmlAttribute(required = true)
    var name: String = ""
    @field:XmlAttribute
    var column: Int = UNDEFINED_COLUMN
    @field:XmlAttribute
    var regex: String? = null
    @field:XmlAttribute
    var type: FieldType = FieldType.STRING
    @field:XmlElements(
        XmlElement(name = "list-format", type = ListDefinition::class),
        XmlElement(name = "date-format", type = DateDefinition::class),
        XmlElement(name = "datetime-format", type = DatetimeDefinition::class),
        XmlElement(name = "attr-list-format", type = AttrListDefinition::class)
    )
    var typeDefinitions: List<Any> = mutableListOf()
    @XmlAttribute(name = "key-field")
    var keyField: Boolean = false
    @XmlAttribute
    var nested: Boolean = false
    @XmlAttribute
    var parent: String? = null
    @XmlAttribute
    var optional: Boolean = false
    @XmlAttribute
    var locale: String? = null

    @XmlTransient
    private val logger = LoggerFactory.getLogger(LOG_NAME)

    override fun validate(parent: Any?) {
        when (type) {
            FieldType.BOOL,
            FieldType.INT,
            FieldType.DOUBLE,
            FieldType.STRING,
            FieldType.TEXT ->
                if (typeDefinitions.isNotEmpty()) {
                    logger.warn("Filed<$name> has simple type that does not need any extra type definition")
                }
            FieldType.DATE ->
                if (typeDefinitions.isEmpty() || !typeDefinitions.any { it is DateDefinition }) {
                    throw ConfigException("Field<$name> has type DATE, but date format is not defined")
                } else if (typeDefinitions.count() > 1 && typeDefinitions.any { it is DateDefinition }) {
                    logger.warn("Field<$name> has unnecessary type definition")
                }
            FieldType.DATETIME ->
                if (typeDefinitions.isEmpty() || !typeDefinitions.any { it is DatetimeDefinition }) {
                    throw ConfigException("Field<$name> has type DATETIME, but date format is not defined")
                } else if (typeDefinitions.count() > 1 && typeDefinitions.any { it is DatetimeDefinition }) {
                    logger.warn("Field<$name> has unnecessary type definition")
                }
            FieldType.BOOL_LIST, FieldType.INT_LIST, FieldType.DOUBLE_LIST, FieldType.STRING_LIST ->
                if (typeDefinitions.isEmpty() || !typeDefinitions.any { it is ListDefinition }) {
                    throw ConfigException("Field<$name> has type *_LIST, but list format is not defined")
                } else if (typeDefinitions.count() > 1 && typeDefinitions.any { it is ListDefinition }) {
                    logger.warn("Field<$name> has unnecessary type definition")
                }
            FieldType.DATE_LIST ->
                if (typeDefinitions.isEmpty()
                    || !typeDefinitions.any { it is DateDefinition }
                    || !typeDefinitions.any { it is ListDefinition }
                ) {
                    throw ConfigException("Field<$name> has type DATE_LIST but it's not fully defined")
                } else if (typeDefinitions.count() > 2
                    && typeDefinitions.any { it is DateDefinition }
                    && typeDefinitions.any { it is ListDefinition }
                ) {
                    logger.warn("Field<$name> has unnecessary type definition")
                }
            FieldType.DATETIME_LIST ->
                if (typeDefinitions.isEmpty()
                    || !typeDefinitions.any { it is DatetimeDefinition }
                    || !typeDefinitions.any { it is ListDefinition }
                ) {
                    throw ConfigException("Field<$name> has type DATETIME_LIST but it's not fully defined")
                } else if (typeDefinitions.count() > 2
                    && typeDefinitions.any { it is DatetimeDefinition }
                    && typeDefinitions.any { it is ListDefinition }
                ) {
                    logger.warn("Field<$name> has unnecessary type definition")
                }
            FieldType.ATTRIBUTE_LIST ->
                if (typeDefinitions.isEmpty() || !typeDefinitions.any { it is AttrListDefinition }) {
                    throw ConfigException("Field<$name> has type ATTRIBUTE_LIST but not fully defined")
                } else if (typeDefinitions.count() > 1 && typeDefinitions.any { it is AttrListDefinition }) {
                    logger.warn("Field<$name> has extra unnecessary type definition")
                }
        }
        if (nested && parent == null) {
            throw ConfigException("Filed<$name> is nested but does not have parent")
        }
        validateThatNestedIsNotKey()
        validateLocale()
    }

    private fun validateLocale() {
        locale?.let {
            val code = it.split("_")
            try {
                val v = Locale(code[0], code[1])
                v.isO3Language
                v.isO3Country
                Unit
            } catch (e: Exception) {
                when (e) {
                    is MissingResourceException,
                    is NullPointerException ->
                        throw ConfigException("Field<$name> has wrong locale value")
                    else -> throw e
                }

            }
        }
    }

    private fun validateThatNestedIsNotKey() {
        if (nested && keyField) {
            throw ConfigException("Field<$name> is nested and therefor can not be key field")
        }
    }

    companion object {
        const val UNDEFINED_COLUMN = -1
    }
}
