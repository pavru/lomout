package net.pototskiy.apps.magemediation.api.database.source

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

class DataEntityWithAttribute(id: EntityID<Int>): IntEntity(id) {
}