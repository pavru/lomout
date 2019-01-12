package net.pototskiy.apps.magemediation.api.database.source

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import org.joda.time.DateTime
import java.util.*

fun Any.castToLong(): Long {
    return when {
        this is Long -> this
        this is Int -> this.toLong()
        this is Float -> this.toLong()
        this is Double -> this.toLong()
        this is Boolean -> if (this) 1L else 0L
        this is String -> this.toLongOrNull() ?: throw DatabaseException("Value can not be cast to Long")
        this is DateTime -> this.millis
        else -> throw DatabaseException("Value can not be cast to Long")
    }
}

fun Any.castToLongOrNull(): Long? {
    return try {
        this.castToLong()
    } catch (e: Exception) {
        null
    }
}

fun Any.castToDouble(): Double {
    return when {
        this is Boolean -> if (this) 1.0 else 0.0
        this is Int -> this.toDouble()
        this is Long -> this.toDouble()
        this is Double -> this
        this is Float -> this.toDouble()
        this is DateTime -> this.millis.toDouble()
        this is String -> this.toDoubleOrNull() ?: throw DatabaseException(
            "Value can not cast to Double"
        )
        else -> throw DatabaseException("Value can not cast to Double")
    }
}

fun Any.castToDoubleOrNull(): Double? {
    return try {
        this.castToDouble()
    } catch (e:Exception) {
        null
    }
}

fun Any.castToBoolean(): Boolean {
    return when {
        this is Boolean -> this
        this is Int -> this != 0
        this is Long -> this != 0L
        this is Float -> this != 0.0f
        this is Double -> this != 0.0
        this is String -> this.isNotBlank()
        else -> throw DatabaseException("Value can not be cast to Boolean")
    }
}

fun Any.castToBooleanOrNull(): Boolean? {
    return try {
        this.castToBoolean()
    } catch (e: Exception) {
        null
    }
}

fun Any.castToDateTime(): DateTime {
    return when {
        this is DateTime -> this
        this is Int -> DateTime(Date(this.toLong()))
        this is Long -> DateTime(Date(this))
        this is Float -> DateTime(Date(this.toLong()))
        this is Double -> DateTime(Date(this.toLong()))
        this is String -> DateTime.parse(this)
        else -> throw DatabaseException("Value can not be cast to DateTime")
    }
}

fun Any.castToDateTimeOrNull(): DateTime? {
    return try {
        this.castToDateTime()
    } catch (e: Exception) {
        null
    }
}