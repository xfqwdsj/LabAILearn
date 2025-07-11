package top.ltfan.labailearn.tool

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberVibrator(): Vibrator = remember { DesktopVibrator() }

class DesktopVibrator : Vibrator {
    override fun vibrate(effect: VibrationEffect) {}
    override fun cancel() {}
}
