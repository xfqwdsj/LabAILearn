package top.ltfan.labailearn.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.serializer
import labailearn.compose_app.generated.resources.Res
import labailearn.compose_app.generated.resources.label_navigation_overview
import labailearn.compose_app.generated.resources.label_navigation_settings
import labailearn.compose_app.generated.resources.label_navigation_tools
import org.jetbrains.compose.resources.StringResource
import top.ltfan.labailearn.ui.component.BlurEnterExit
import top.ltfan.labailearn.ui.component.calculateFromAdaptiveInfo
import top.ltfan.labailearn.ui.pages.main.OverviewPage
import top.ltfan.labailearn.ui.pages.main.SettingsPage
import top.ltfan.labailearn.ui.pages.main.ToolsPage
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalSharedTransitionApi::class)
@Serializable
abstract class Route {
    protected constructor()

    // 移除未使用变量的名字会导致Android Dex编译失败
    context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) abstract fun NavGraphBuilder.builder(
        windowAdaptiveInfo: WindowAdaptiveInfo
    )

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

            context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) override fun NavGraphBuilder.builder(
                windowAdaptiveInfo: WindowAdaptiveInfo
            ) {
                composableWithSlideTransition<Overview>(windowAdaptiveInfo) { BlurEnterExit { viewModel.OverviewPage() } }
            }
        }

        @Serializable
        open class Tools : Main() {
            @Transient
            override val label = Res.string.label_navigation_tools

            @Transient
            override val icon = Icons.Default.Hardware

            context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) override fun NavGraphBuilder.builder(
                windowAdaptiveInfo: WindowAdaptiveInfo
            ) {
                composableWithSlideTransition<Tools>(windowAdaptiveInfo) { BlurEnterExit { viewModel.ToolsPage() } }
            }

            abstract class Tool : Tools() {
                @OptIn(ExperimentalUuidApi::class)
                abstract val uuid: Uuid
            }
        }

        @Serializable
        open class Settings : Main() {
            @Transient
            override val label = Res.string.label_navigation_settings

            @Transient
            override val icon = Icons.Default.Settings

            context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) override fun NavGraphBuilder.builder(
                windowAdaptiveInfo: WindowAdaptiveInfo
            ) {
                composableWithSlideTransition<Settings>(windowAdaptiveInfo) { BlurEnterExit { viewModel.SettingsPage() } }
            }
        }

        @OptIn(InternalSerializationApi::class)
        context(viewModel: AppViewModel) inline fun <reified T : Any> NavGraphBuilder.composableWithSlideTransition(
            windowAdaptiveInfo: WindowAdaptiveInfo,
            typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
            deepLinks: List<NavDeepLink> = emptyList(),
            noinline sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards SizeTransform?)? = null,
            noinline content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
        ) {
            val pages = viewModel.main.pages.map { it::class.serializer().descriptor.serialName }
            val navigationSuiteType = NavigationSuiteType.calculateFromAdaptiveInfo(windowAdaptiveInfo)

            val getDirection: AnimatedContentTransitionScope<NavBackStackEntry>.() -> AnimatedContentTransitionScope.SlideDirection =
                getDirection@{
                    val from = pages.indexOf(initialState.destination.route)
                    val to = pages.indexOf(targetState.destination.route)

                    if (to > from) {
                        if (navigationSuiteType == NavigationSuiteType.NavigationBar) {
                            AnimatedContentTransitionScope.SlideDirection.Start
                        } else {
                            AnimatedContentTransitionScope.SlideDirection.Up
                        }
                    } else {
                        if (navigationSuiteType == NavigationSuiteType.NavigationBar) {
                            AnimatedContentTransitionScope.SlideDirection.End
                        } else {
                            AnimatedContentTransitionScope.SlideDirection.Down
                        }
                    }
                }

            val animationSpec = spring(
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = IntOffset.VisibilityThreshold,
            )

            composable<T>(
                typeMap = typeMap,
                deepLinks = deepLinks,
                enterTransition = { slideIntoContainer(getDirection(), animationSpec) },
                exitTransition = { slideOutOfContainer(getDirection(), animationSpec) },
                popEnterTransition = { slideIntoContainer(getDirection(), animationSpec) },
                popExitTransition = { slideOutOfContainer(getDirection(), animationSpec) },
                sizeTransform = sizeTransform,
                content = content
            )
        }
    }
}
