package net.pototskiy.apps.lomout.api.entity.values

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

/**
 * Double fraction as Double
 */
val Double.fraction: Double
    inline get() = (abs(this) - floor(abs(this))) * sign(this)

/**
 * Floor and cast to Long
 *
 * @receiver Double
 * @return Long
 */
fun Double.floorToLong(): Long = floor(this).toLong()

/**
 * Cast Double to Long
 *
 * @receiver Double
 * @return Long
 * @throws TypeCastException If double value fraction part is not zero
 */
fun Double.doubleToLong(): Long {
    return if (this.fraction == 0.0) {
        this.floorToLong()
    } else {
        throw TypeCastException("Double value<$this> has non zero fraction and can not converted to Long")
    }
}
