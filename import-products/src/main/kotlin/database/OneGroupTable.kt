package database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object OneGroupTable : IntIdTable("offline_groups") {
    val code = varchar("code", 100).uniqueIndex()
    val name = varchar("name", 500).index()
    val created = date("created").index()
    val updated = date("update").index()
    val absentAge = integer("absent_age").default(0).index()
    val touched = bool("touches").default(true).index()
}

class OneGroupEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<OneGroupEntity>(OneGroupTable)

    var code by OneGroupTable.code
    var name by OneGroupTable.name
    var created by OneGroupTable.created
    var updated by OneGroupTable.updated
    var absentAge by OneGroupTable.absentAge
    var touched by OneGroupTable.touched
}