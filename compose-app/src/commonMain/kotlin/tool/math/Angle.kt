package top.ltfan.labailearn.tool.math

import kotlin.math.*

sealed class Angle : Comparable<Angle> {
    data class Degrees(val value: Double) : Angle() {
        constructor(value: Number) : this(value.toDouble())

        fun toRadians(): Radians = Radians(value * (PI / 180))
    }

    data class Radians(val value: Double) : Angle() {
        constructor(value: Number) : this(value.toDouble())

        fun toDegrees(): Degrees = Degrees(value * (180 / PI))
    }

    val degrees
        get() = when (this) {
            is Degrees -> this
            is Radians -> toDegrees()
        }.value

    val radians
        get() = when (this) {
            is Degrees -> toRadians()
            is Radians -> this
        }.value

    val normalized
        get() = when (this) {
            is Degrees -> Degrees(value % 360).let { if (it.value < 0) Degrees(it.value + 360) else it }
            is Radians -> Radians(value % (2 * PI)).let { if (it.value < 0) Radians(it.value + 2 * PI) else it }
        }

    val sin inline get() = sin(radians)
    val cos inline get() = cos(radians)
    val tan inline get() = tan(radians)

    val asin inline get() = fromRadians(asin(sin))
    val acos inline get() = fromRadians(acos(cos))
    val atan inline get() = fromRadians(atan(tan))

    companion object {
        val ZERO: Angle = fromDegrees(0)

        fun fromDegrees(value: Number): Angle = Degrees(value.toDouble())
        fun fromRadians(value: Number): Angle = Radians(value.toDouble())
    }

    operator fun plus(other: Angle): Angle {
        return when (this) {
            is Degrees -> when (other) {
                is Degrees -> Degrees(this.value + other.value)
                is Radians -> Degrees(this.value + other.toDegrees().value)
            }

            is Radians -> when (other) {
                is Degrees -> Radians(this.value + other.toRadians().value)
                is Radians -> Radians(this.value + other.value)
            }
        }
    }

    operator fun minus(other: Angle): Angle {
        return when (this) {
            is Degrees -> when (other) {
                is Degrees -> Degrees(this.value - other.value)
                is Radians -> Degrees(this.value - other.toDegrees().value)
            }

            is Radians -> when (other) {
                is Degrees -> Radians(this.value - other.toRadians().value)
                is Radians -> Radians(this.value - other.value)
            }
        }
    }

    operator fun times(factor: Number): Angle {
        return when (this) {
            is Degrees -> Degrees(this.value * factor.toDouble())
            is Radians -> Radians(this.value * factor.toDouble())
        }
    }

    operator fun div(divisor: Angle): Double {
        return when (this) {
            is Degrees -> when (divisor) {
                is Degrees -> this.value / divisor.value
                is Radians -> this.value / divisor.toDegrees().value
            }

            is Radians -> when (divisor) {
                is Degrees -> this.value / divisor.toRadians().value
                is Radians -> this.value / divisor.value
            }
        }
    }

    operator fun div(divisor: Number): Angle {
        return when (this) {
            is Degrees -> Degrees(this.value / divisor.toDouble())
            is Radians -> Radians(this.value / divisor.toDouble())
        }
    }

    operator fun rem(other: Angle): Angle {
        return when (this) {
            is Degrees -> when (other) {
                is Degrees -> Degrees(this.value % other.value)
                is Radians -> Degrees(this.value % other.toDegrees().value)
            }

            is Radians -> when (other) {
                is Degrees -> Radians(this.value % other.toRadians().value)
                is Radians -> Radians(this.value % other.value)
            }
        }
    }

    operator fun unaryMinus(): Angle {
        return when (this) {
            is Degrees -> Degrees(-this.value)
            is Radians -> Radians(-this.value)
        }
    }

    override operator fun compareTo(other: Angle): Int {
        val epsilon = 1e-10
        val diff = when (this) {
            is Degrees -> when (other) {
                is Degrees -> this.value - other.value
                is Radians -> this.value - other.toDegrees().value
            }

            is Radians -> when (other) {
                is Degrees -> this.value - other.toRadians().value
                is Radians -> this.value - other.value
            }
        }
        return when {
            abs(diff) < epsilon -> 0
            diff > 0 -> 1
            else -> -1
        }
    }

    override fun toString(): String {
        return when (this) {
            is Degrees -> "$value°"
            is Radians -> "${toDegrees()}°"
        }
    }
}

val Number.degrees get() = Angle.fromDegrees(this)
val Number.radians get() = Angle.fromRadians(this)
val Number.piRadians get() = Angle.fromRadians(toDouble() * PI)

operator fun Number.times(angle: Angle) = angle * this

val Number.asin get() = Angle.fromRadians(asin(toDouble()))
val Number.acos get() = Angle.fromRadians(acos(toDouble()))
val Number.atan get() = Angle.fromRadians(atan(toDouble()))

fun atan2(y: Number, x: Number) = Angle.fromRadians(kotlin.math.atan2(y.toDouble(), x.toDouble()))
