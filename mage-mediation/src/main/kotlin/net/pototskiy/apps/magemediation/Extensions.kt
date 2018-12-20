package net.pototskiy.apps.magemediation

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

val Double.fraction: Double
    inline get() = (abs(this) - floor(abs(this))) * sign(this)

fun Double.floorToLong(): Long = floor(this).toLong()
