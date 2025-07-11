package top.ltfan.labailearn.tool

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.window

@Composable
actual fun rememberVibrator(): Vibrator = remember { BrowserVibrator() }

class BrowserVibrator : Vibrator {
    override fun vibrate(effect: VibrationEffect) { window.navigator.vibrate(effect.pattern) }

    override fun cancel() {}

    val VibrationEffect.pattern
        get() = when (this) {
            is VibrationEffect.Composed -> primitives.map { it.type.duration.inWholeMilliseconds.toInt().toJsNumber() }
        }.toJsArray()
}
