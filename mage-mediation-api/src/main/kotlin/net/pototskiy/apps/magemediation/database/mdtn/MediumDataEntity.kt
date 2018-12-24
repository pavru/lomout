package net.pototskiy.apps.magemediation.database.mdtn

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

abstract class MediumDataEntity(id: EntityID<Int>): IntEntity(id)