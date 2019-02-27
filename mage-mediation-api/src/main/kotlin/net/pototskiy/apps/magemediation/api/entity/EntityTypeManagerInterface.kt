package net.pototskiy.apps.magemediation.api.entity

// TODO: 18.02.2019 define parameters
interface EntityTypeManagerInterface {
    fun getEntityType(type: String): EType?
    fun createEntityType(
        name: String,
        inheritances: List<ETypeInheritance>,
        attributes: AttributeCollection,
        open: Boolean
    ): EType

    fun refineEntityAttributes(eType: String, attributes: AttributeCollection)
    fun refineEntityAttributes(eType: EType, attributes: AttributeCollection)
    fun refineEntityAttributes(eType: String, attribute: Attribute<*>)
    fun refineEntityAttributes(eType: EType, attribute: Attribute<*>)
    fun removeEntityType(eType: String)
    fun removeEntityType(eType: EType)
}
