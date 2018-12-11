package database

import importer.cctu
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object OneProductTable : IntIdTable("offline_products") {
    val group = reference(cctu("group"), OneGroupTable)
    val oneSku = varchar(cctu("oneSku"), 100).uniqueIndex()
    val catalogSku = varchar(cctu("catalogSku"), 100)
    val russianName = varchar(cctu("russianName"), 300)
    val englishName = varchar(cctu("englishName"), 300).nullable()
    val manufacturer = varchar(cctu("manufacturer"), 300).nullable()
    val countryOfManufacture = varchar(cctu("countryOfManufacture"), 300).nullable()
    val machine = varchar(cctu("machine"), 300).nullable()
    val machineVendor = varchar(cctu("machineVendor"), 300).nullable()
    val machineUnit = varchar(cctu("machineUnit"), 300).nullable()
    val weight = double(cctu("weight")).nullable()
    val height = double(cctu("height")).nullable()
    val width = double(cctu("width")).nullable()
    val length = double(cctu("length")).nullable()

    val created = date(cctu("created"))
    val updated = date(cctu("updated"))
    val absentAge = integer(cctu("absentAge")).default(0)
    val touched = bool(cctu("touched"))
}

class OneProductEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OneProductEntity>(OneProductTable)

    var group by OneGroupEntity referencedOn OneProductTable.group
    val oneSku by OneProductTable.oneSku
    val catalogSku by OneProductTable.catalogSku
    val russianName by OneProductTable.russianName
    val englishName by OneProductTable.englishName
    val manufacturer by OneProductTable.manufacturer
    val countryOfManufacture by OneProductTable.countryOfManufacture
    val machine by OneProductTable.machine
    val machineVendor by OneProductTable.machineVendor
    val machineUnit by OneProductTable.machineUnit
    val weight by OneProductTable.weight
    val height by OneProductTable.height
    val width by OneProductTable.width
    val length by OneProductTable.length

    val created by OneProductTable.created
    val updated by OneProductTable.updated
    val absentAge by OneProductTable.absentAge
    val touched by OneProductTable.touched
}