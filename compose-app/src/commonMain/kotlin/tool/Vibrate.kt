package top.ltfan.labailearn.tool

import androidx.compose.runtime.Composable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


sealed class VibrationEffect {
    companion object {
        fun Composed(builder: VibrationEffectCompositionBuilder.() -> Unit): Composed {
            val builder = VibrationEffectCompositionBuilder().apply(builder)
            return Composed(builder.primitives)
        }
    }

    data class Composed(
        val primitives: List<Primitive>
    ) : VibrationEffect() {
        operator fun plus(primitive: Primitive) = Composed(primitives + primitive)
    }
}

class VibrationEffectCompositionBuilder {
    val primitives: MutableList<Primitive> = mutableListOf()

    fun addPrimitive(
        type: PrimitiveType, scale: Float = 1f, delay: Duration = Duration.ZERO, delayType: DelayType = DelayType.Pause
    ): Primitive {
        val primitive = Primitive(type, scale, delay, delayType)
        primitives.add(primitive)
        return primitive
    }
}

data class Primitive(
    val type: PrimitiveType,
    val scale: Float = 1f,
    val delay: Duration = Duration.ZERO,
    val delayType: DelayType = DelayType.Pause
)

enum class PrimitiveType(val duration: Duration) {
    Click(50.milliseconds),
    Thud(100.milliseconds),
    Spin(70.milliseconds),
    QuickRise(40.milliseconds),
    SlowRise(120.milliseconds),
    QuickFall(60.milliseconds),
    Tick(30.milliseconds),
    LowTick(30.milliseconds);

    val durationMillis: Long
        get() = duration.inWholeMilliseconds
}

enum class DelayType { Pause, RelativeStartOffset }

interface Vibrator {
    fun vibrate(effect: VibrationEffect)
    fun vibrateComposed(builder: VibrationEffectCompositionBuilder.() -> Unit) =
        vibrate(VibrationEffect.Composed(builder))

    fun cancel()
}

@Composable
expect fun rememberVibrator(): Vibrator
