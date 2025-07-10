package top.ltfan.labailearn

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import labailearn.compose_app.generated.resources.Res
import labailearn.compose_app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import top.ltfan.labailearn.ui.*
import top.ltfan.labailearn.ui.component.*
import top.ltfan.labailearn.ui.theme.AppTheme
import kotlin.uuid.ExperimentalUuidApi

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class,
    ExperimentalSharedTransitionApi::class, ExperimentalUuidApi::class,
)
@Composable
fun App(navController: NavHostController? = null) {
    with(viewModel { AppViewModel() }) {
        with(navController ?: rememberNavController()) {
            with(remember { HazeState() }) {
                AppTheme {
                    AdaptiveScaffold(
                        topBar = { contentPadding ->
                            val currentPage by currentPageAsState
                            AnimatedSlide(
                                active = main.pages.any { it == currentPage },
                                slideDirection = SlideDirection.Top,
                            ) {
                                CenterAlignedTopAppBar(
                                    title = { Text(stringResource(Res.string.app_name)) },
                                    modifier = Modifier.appBarHazeEffect(HazeAppBarType.Top).padding(contentPadding),
                                    colors = TopAppBarDefaults.largeTopAppBarColors(Color.Transparent),
                                    windowInsets = AppWindowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                                )
                            }
                        },
                        navigationSuite = { layoutType ->
                            val currentPage by currentPageAsState
                            NavigationSuite(
                                modifier = Modifier.run {
                                    if (layoutType == NavigationSuiteType.NavigationBar) {
                                        appBarHazeEffect(HazeAppBarType.Bottom)
                                    } else this
                                },
                                layoutType = layoutType,
                                containers = NavigationSuiteDefaults.containers(
                                    navigationBarContainer = { content ->
                                        AnimatedSlide(
                                            active = main.pages.any { page -> page == currentPage },
                                            slideDirection = SlideDirection.Bottom,
                                            content = content
                                        )
                                    },
                                ),
                                colors = NavigationSuiteDefaults.colors(
                                    navigationBarContainerColor = Color.Transparent,
                                    navigationRailContainerColor = Color.Transparent,
                                    navigationDrawerContainerColor = Color.Transparent
                                ),
                                windowInsets = NavigationSuiteDefaults.windowInsetsWithDefaultSides(AppWindowInsets),
                            ) {
                                main.pages.forEach { route ->
                                    item(
                                        selected = currentPage == route,
                                        onClick = { if (currentPage != route) main.navigate(route) },
                                        icon = { Icon(route.icon, contentDescription = stringResource(route.label)) },
                                        label = { Text(stringResource(route.label)) },
                                    )
                                }
                            }
                        },
                        contentWindowInsets = AppWindowInsets,
                    ) { padding ->
                        val windowAdaptiveInfo = currentWindowAdaptiveInfo()
                        val layoutDirection = LocalLayoutDirection.current
                        val paddingStart = padding.calculateStartPadding(layoutDirection)
                        val insetsPaddingStart =
                            AppWindowInsets.asPaddingValues().calculateStartPadding(layoutDirection)

                        SharedTransitionLayout {
                            NavHost(startDestination = main.pages.first(), modifier = Modifier.hazeSource()) {
                                with(padding) { main.pages.forEach { with(it) { builder(windowAdaptiveInfo) } } }

                                val subpagePadding = PaddingValues(start = paddingStart - insetsPaddingStart)
                                with(subpagePadding) {
                                    with(tools) {
                                        tools.forEach { with(it.key.route) { builder(windowAdaptiveInfo) } }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
