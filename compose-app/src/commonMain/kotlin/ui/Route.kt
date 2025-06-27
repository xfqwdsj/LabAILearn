package top.ltfan.labailearn.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import labailearn.compose_app.generated.resources.Res
import labailearn.compose_app.generated.resources.label_navigation_overview
import labailearn.compose_app.generated.resources.label_navigation_settings
import labailearn.compose_app.generated.resources.label_navigation_tools
import org.jetbrains.compose.resources.StringResource
import top.ltfan.labailearn.ui.component.BlurEnterExit
import top.ltfan.labailearn.ui.pages.main.OverviewPage
import top.ltfan.labailearn.ui.pages.main.SettingsPage
import top.ltfan.labailearn.ui.pages.main.ToolsPage
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

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
                composableWithSlideTransition<Overview> { BlurEnterExit { viewModel.OverviewPage() } }
            }
        }

        @Serializable
        open class Tools : Main() {
            @Transient
            override val label = Res.string.label_navigation_tools

            @Transient
            override val icon = Icons.Default.Hardware

            context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) override fun NavGraphBuilder.builder() {
                composableWithSlideTransition<Tools> { BlurEnterExit { viewModel.ToolsPage() } }
            }
        }

        @Serializable
        open class Settings : Main() {
            @Transient
            override val label = Res.string.label_navigation_settings

            @Transient
            override val icon = Icons.Default.Settings

            context(viewModel: AppViewModel, n: NavController, s: SharedTransitionScope, p: PaddingValues) override fun NavGraphBuilder.builder() {
                composableWithSlideTransition<Settings> { BlurEnterExit { viewModel.SettingsPage() } }
            }
        }

        context(viewModel: AppViewModel) inline fun <reified T : Any> NavGraphBuilder.composableWithSlideTransition(
            typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
            deepLinks: List<NavDeepLink> = emptyList(),
            noinline sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards SizeTransform?)? = null,
            noinline content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
        ) {
            val pages = viewModel.main.pages.map { it::class.qualifiedName }

            val getDirection: AnimatedContentTransitionScope<NavBackStackEntry>.() -> AnimatedContentTransitionScope.SlideDirection =
                getDirection@{
                    val from = try {
                        println(initialState)
                        pages.indexOf(initialState.destination.route)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        return@getDirection AnimatedContentTransitionScope.SlideDirection.Start
                    }

                    val to = try {
                        println(targetState)
                        pages.indexOf(targetState.destination.route)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        return@getDirection AnimatedContentTransitionScope.SlideDirection.Start
                    }

                    if (to > from) {
                        AnimatedContentTransitionScope.SlideDirection.Start
                    } else {
                        AnimatedContentTransitionScope.SlideDirection.End
                    }
                }

            composable<T>(
                typeMap = typeMap,
                deepLinks = deepLinks,
                enterTransition = { slideIntoContainer(getDirection()) + fadeIn() },
                exitTransition = { slideOutOfContainer(getDirection()) + fadeOut() },
                popEnterTransition = { slideIntoContainer(getDirection()) + fadeIn() },
                popExitTransition = { slideOutOfContainer(getDirection()) + fadeOut() },
                sizeTransform = sizeTransform,
                content = content
            )
        }
    }
}
