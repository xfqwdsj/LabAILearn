package top.ltfan.labailearn.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import labailearn.compose_app.generated.resources.Res
import labailearn.compose_app.generated.resources.label_navigation_overview
import labailearn.compose_app.generated.resources.label_navigation_settings
import labailearn.compose_app.generated.resources.label_navigation_tools
import org.jetbrains.compose.resources.StringResource
import top.ltfan.labailearn.ui.pages.main.OverviewPage
import top.ltfan.labailearn.ui.pages.main.SettingsPage
import top.ltfan.labailearn.ui.pages.main.ToolsPage

@OptIn(ExperimentalSharedTransitionApi::class)
@Serializable
abstract class Route {
    protected constructor()

    // 移除未使用变量的名字会导致Android Dex编译失败
    context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) abstract fun NavGraphBuilder.builder()

    @Serializable
    abstract class Main : Route() {
        abstract val label: StringResource
        abstract val icon: ImageVector

        @Serializable
        open class Overview : Main() {
            @Transient
            override val label = Res.string.label_navigation_overview

            @Transient
            override val icon = Icons.Default.SpaceDashboard

            context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) override fun NavGraphBuilder.builder() {
                composable<Overview> { viewModel.OverviewPage() }
            }
        }

        @Serializable
        open class Tools : Main() {
            @Transient
            override val label = Res.string.label_navigation_tools

            @Transient
            override val icon = Icons.Default.Hardware

            context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) override fun NavGraphBuilder.builder() {
                composable<Tools> { viewModel.ToolsPage() }
            }
        }

        @Serializable
        open class Settings : Main() {
            @Transient
            override val label = Res.string.label_navigation_settings

            @Transient
            override val icon = Icons.Default.Settings

            context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) override fun NavGraphBuilder.builder() {
                composable<Settings> { viewModel.SettingsPage() }
            }
        }
    }
}
