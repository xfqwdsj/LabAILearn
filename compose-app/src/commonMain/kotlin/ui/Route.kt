package top.ltfan.labailearn.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import labailearn.compose_app.generated.resources.Res
import labailearn.compose_app.generated.resources.name_navigation_overview
import labailearn.compose_app.generated.resources.name_navigation_settings
import labailearn.compose_app.generated.resources.name_navigation_tools
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed class Route {
    @Serializable
    sealed class Main : Route() {
        abstract val label: StringResource
        abstract val icon: ImageVector

        @Serializable
        object Overview : Main() {
            override val label = Res.string.name_navigation_overview
            override val icon = Icons.Default.SpaceDashboard
        }

        @Serializable
        object Tools : Main() {
            override val label = Res.string.name_navigation_tools
            override val icon = Icons.Default.Hardware
        }

        @Serializable
        object Settings : Main() {
            override val label = Res.string.name_navigation_settings
            override val icon = Icons.Default.Settings
        }
    }
}
