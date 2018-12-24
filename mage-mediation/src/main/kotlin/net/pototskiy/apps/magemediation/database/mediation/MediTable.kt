package net.pototskiy.apps.magemediation.database.mediation

import org.jetbrains.exposed.dao.IntIdTable

abstract class MediTable(name: String) : IntIdTable(name) {

}