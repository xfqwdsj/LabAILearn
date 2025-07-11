package top.ltfan.labailearn.tool

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@Composable
actual fun rememberVibrator(): Vibrator {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val androidVibrator = remember(context) { context.getSystemService(android.os.Vibrator::class.java) }
    val vibrator = remember(androidVibrator, coroutineScope) { AndroidVibrator(androidVibrator, coroutineScope) }

    DisposableEffect(vibrator) { onDispose { vibrator.clear() } }

    return vibrator
}

class AndroidVibrator(
    private val vibrator: android.os.Vibrator,
    private val coroutineScope: CoroutineScope,
) : Vibrator {
    private val nextEffect = MutableStateFlow<VibrationEffect?>(null)
    private val job = coroutineScope.launch {
        nextEffect.collect {
            it?.let { effect ->
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && effect.isLevel4Supported -> level4(effect)
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> level3(effect)
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> level2(effect)
                    else -> level1(effect)
                }
            }
        }
    }

    override fun vibrate(effect: VibrationEffect) {
        coroutineScope.launch { nextEffect.emit(effect) }
    }

    private val VibrationEffect.isLevel4Supported: Boolean
        @RequiresApi(Build.VERSION_CODES.R) inline get() = when (this) {
            is VibrationEffect.Composed -> vibrator.areAllPrimitivesSupported(
                *primitives.map { it.type.androidPrimitiveType ?: return false }.toIntArray(),
            )
        }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun level4(effect: VibrationEffect) {
        when (effect) {
            is VibrationEffect.Composed -> effect.androidComposedVibrationEffect?.let { vibrator.vibrate(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun level3(effect: VibrationEffect) {
        when (effect) {
            is VibrationEffect.Composed -> effect.primitives.forEach { primitive ->
                delay(primitive.delay)
                primitive.type.androidPredefinedEffect?.let { vibrator.vibrate(it) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun level2(effect: VibrationEffect) {
        when (effect) {
            is VibrationEffect.Composed -> effect.primitives.forEach { primitive ->
                delay(primitive.delay)
                primitive.type.androidOneShotEffect?.let { vibrator.vibrate(it) }
            }
        }
    }

    private suspend fun level1(effect: VibrationEffect) {
        when (effect) {
            is VibrationEffect.Composed -> effect.primitives.forEach { primitive ->
                delay(primitive.delay)
                @Suppress("DEPRECATION") vibrator.vibrate(primitive.type.durationMillis)
            }
        }
    }

    override fun cancel() = vibrator.cancel()

    fun clear() {
        job.cancel()
    }

    private val VibrationEffect.androidComposedVibrationEffect
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && this is VibrationEffect.Composed) {
            val composition = android.os.VibrationEffect.startComposition()
            for (primitive in primitives) {
                val androidPrimitive = primitive.type.androidPrimitiveType ?: continue

                val delay = primitive.delay.inWholeMilliseconds.toInt()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                    val androidDelayType = primitive.delayType.androidDelayType ?: continue
                    composition.addPrimitive(androidPrimitive, primitive.scale, delay, androidDelayType)
                } else {
                    composition.addPrimitive(androidPrimitive, primitive.scale, delay)
                }
            }
            composition.compose()
        } else null

    private val PrimitiveType.androidPrimitiveType
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (this) {
                PrimitiveType.Click -> android.os.VibrationEffect.Composition.PRIMITIVE_CLICK
                PrimitiveType.Thud -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    android.os.VibrationEffect.Composition.PRIMITIVE_THUD
                } else {
                    android.os.VibrationEffect.Composition.PRIMITIVE_CLICK
                }

                PrimitiveType.Spin -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    android.os.VibrationEffect.Composition.PRIMITIVE_SPIN
                } else {
                    android.os.VibrationEffect.Composition.PRIMITIVE_QUICK_RISE
                }

                PrimitiveType.QuickRise -> android.os.VibrationEffect.Composition.PRIMITIVE_QUICK_RISE
                PrimitiveType.SlowRise -> android.os.VibrationEffect.Composition.PRIMITIVE_SLOW_RISE
                PrimitiveType.QuickFall -> android.os.VibrationEffect.Composition.PRIMITIVE_QUICK_FALL
                PrimitiveType.Tick -> android.os.VibrationEffect.Composition.PRIMITIVE_TICK
                PrimitiveType.LowTick -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    android.os.VibrationEffect.Composition.PRIMITIVE_LOW_TICK
                } else {
                    android.os.VibrationEffect.Composition.PRIMITIVE_TICK
                }
            }
        } else null

    private val PrimitiveType.androidPredefinedEffect
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            android.os.VibrationEffect.createPredefined(
                when (this) {
                    PrimitiveType.Click -> android.os.VibrationEffect.EFFECT_CLICK
                    PrimitiveType.Thud -> android.os.VibrationEffect.EFFECT_HEAVY_CLICK
                    PrimitiveType.Spin -> android.os.VibrationEffect.EFFECT_DOUBLE_CLICK
                    PrimitiveType.QuickRise -> android.os.VibrationEffect.EFFECT_DOUBLE_CLICK
                    PrimitiveType.SlowRise -> android.os.VibrationEffect.EFFECT_HEAVY_CLICK
                    PrimitiveType.QuickFall -> android.os.VibrationEffect.EFFECT_DOUBLE_CLICK
                    PrimitiveType.Tick -> android.os.VibrationEffect.EFFECT_TICK
                    PrimitiveType.LowTick -> android.os.VibrationEffect.EFFECT_TICK
                }
            )
        } else null

    private val PrimitiveType.androidOneShotEffect
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (this) {
                PrimitiveType.Click -> android.os.VibrationEffect.createOneShot(
                    durationMillis, android.os.VibrationEffect.DEFAULT_AMPLITUDE
                )

                PrimitiveType.Thud -> android.os.VibrationEffect.createOneShot(
                    durationMillis, android.os.VibrationEffect.DEFAULT_AMPLITUDE
                )

                PrimitiveType.Spin -> android.os.VibrationEffect.createOneShot(
                    durationMillis, android.os.VibrationEffect.DEFAULT_AMPLITUDE
                )

                PrimitiveType.QuickRise -> android.os.VibrationEffect.createOneShot(
                    durationMillis, android.os.VibrationEffect.DEFAULT_AMPLITUDE
                )

                PrimitiveType.SlowRise -> android.os.VibrationEffect.createOneShot(
                    durationMillis, android.os.VibrationEffect.DEFAULT_AMPLITUDE
                )

                PrimitiveType.QuickFall -> android.os.VibrationEffect.createOneShot(
                    durationMillis, android.os.VibrationEffect.DEFAULT_AMPLITUDE
                )

                PrimitiveType.Tick -> android.os.VibrationEffect.createOneShot(
                    durationMillis, android.os.VibrationEffect.DEFAULT_AMPLITUDE
                )

                PrimitiveType.LowTick -> android.os.VibrationEffect.createOneShot(durationMillis, 50)
            }
        } else null

    private val DelayType.androidDelayType
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            when (this) {
                DelayType.Pause -> android.os.VibrationEffect.Composition.DELAY_TYPE_PAUSE
                DelayType.RelativeStartOffset -> android.os.VibrationEffect.Composition.DELAY_TYPE_RELATIVE_START_OFFSET
            }
        } else null
}
