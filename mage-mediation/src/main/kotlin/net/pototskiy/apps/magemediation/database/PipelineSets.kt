package net.pototskiy.apps.magemediation.database

import net.pototskiy.apps.magemediation.api.database.schema.SourceEntities
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function

private const val UUID_LENGTH = 50


object PipelineSets : IntIdTable("pipeline_set") {

    val setID = varchar("set_id", UUID_LENGTH)
    val entityID = reference("entity_id",SourceEntities, ReferenceOption.CASCADE)
    val isMatched = bool("is_matched").default(false).index()

    init {
        uniqueIndex(setID, entityID)
    }
}

class PipelineSet(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PipelineSet>(PipelineSets)

    var setID by PipelineSets.setID
    var entityID by PipelineSets.entityID
    var isMatched by PipelineSets.isMatched
}

class StringConst(private val value: String) : Function<String>(VarCharColumnType()) {
    override fun toSQL(queryBuilder: QueryBuilder): String = "'$value'"
}

class BooleanConst(private val value: Boolean) : Function<Boolean>(BooleanColumnType()) {
    override fun toSQL(queryBuilder: QueryBuilder): String = "${if (value) 1 else 0}"
}

