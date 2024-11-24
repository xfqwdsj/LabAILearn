package top.ltfan.labailearn.ui.theme

import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun AppTheme(dark: Boolean, content: @Composable () -> Unit) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
        val context = LocalContext.current
        val colorScheme = if (dark) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
        Theme(colorScheme = colorScheme, content = content)
    } else {
        DayNightTheme(dark = dark, content = content)
    }
}
