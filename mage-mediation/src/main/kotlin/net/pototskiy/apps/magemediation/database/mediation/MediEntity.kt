package net.pototskiy.apps.magemediation.database.mediation

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity

abstract class MediEntity(id: EntityID<Int>): IntEntity(id)