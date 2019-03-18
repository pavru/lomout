package net.pototskiy.apps.lomout.api.entity.values

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

val Double.fraction: Double
    inline get() = (abs(this) - floor(abs(this))) * sign(this)

fun Double.floorToLong(): Long = floor(this).toLong()

fun Double.doubleToLong(): Long {
    return if (this.fraction == 0.0) {
        this.floorToLong()
    } else {
        throw TypeCastException("Double value<$this> has non zero fraction and can not converted to Long")
    }
}
