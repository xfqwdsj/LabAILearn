package top.ltfan.labailearn.ui.theme

import androidx.compose.runtime.Composable

@Composable
actual fun AppTheme(dark: Boolean, content: @Composable () -> Unit) {
    DayNightTheme(dark = dark, content = content)
}
