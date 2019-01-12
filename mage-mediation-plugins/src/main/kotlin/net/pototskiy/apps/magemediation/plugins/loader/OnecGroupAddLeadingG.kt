package net.pototskiy.apps.magemediation.plugins.loader

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.plugable.loader.FieldTransformer

class OnecGroupAddLeadingG : FieldTransformer<String> {
    override fun transform(value: String): String {
        return if (value.isNotBlank()) {
            val intID = value.toIntOrNull()
                ?: throw DatabaseException("Group code<$value> can not be transformed, it is not integer number")
            String.format("G%03d", intID)
        } else {
            value
        }
    }
}