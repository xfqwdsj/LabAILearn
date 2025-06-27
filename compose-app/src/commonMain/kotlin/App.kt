package top.ltfan.labailearn

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.chrisbanes.haze.*
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import labailearn.compose_app.generated.resources.Res
import labailearn.compose_app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import top.ltfan.labailearn.ui.AppViewModel
import top.ltfan.labailearn.ui.AppWindowInsets
import top.ltfan.labailearn.ui.component.*
import top.ltfan.labailearn.ui.theme.AppTheme

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
fun App() {
    val viewModel = viewModel { AppViewModel() }
    val navController = rememberNavController()
    with(viewModel) {
        with(navController) {
            AppTheme {
                val hazeState = remember { HazeState() }
                AdaptiveScaffold(
                    topBar = { contentPadding ->
                        AnimatedSlide(
                            active = main.pages.any { it == currentPage },
                            slideDirection = SlideDirection.Top,
                        ) {
                            CenterAlignedTopAppBar(
                                title = { Text(stringResource(Res.string.app_name)) },
                                modifier = Modifier.hazeEffect(
                                    hazeState, style = HazeMaterials.regular()
                                ) {
                                    inputScale = HazeInputScale.Auto
                                    progressive =
                                        HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                                }.padding(contentPadding),
                                colors = TopAppBarDefaults.largeTopAppBarColors(Color.Transparent),
                                windowInsets = AppWindowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                            )
                        }
                    },
                    navigationSuite = { layoutType ->
                        val currentPage = currentPage
                        NavigationSuite(
                            modifier = Modifier.run {
                                if (layoutType == NavigationSuiteType.NavigationBar) {
                                    hazeEffect(
                                        hazeState, style = HazeMaterials.regular()
                                    ) {
                                        inputScale = HazeInputScale.Auto
                                        progressive =
                                            HazeProgressive.verticalGradient(startIntensity = 0f, endIntensity = 1f)
                                    }
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
                                    onClick = { main.navigate(route) },
                                    icon = { Icon(route.icon, contentDescription = stringResource(route.label)) },
                                    label = { Text(stringResource(route.label)) },
                                )
                            }
                        }
                    },
                    contentWindowInsets = AppWindowInsets,
                ) { padding ->
                    val layoutDirection = LocalLayoutDirection.current
                    val paddingStart = padding.calculateStartPadding(layoutDirection)
                    val insetsPaddingStart = AppWindowInsets.asPaddingValues().calculateStartPadding(layoutDirection)

                    SharedTransitionLayout {
                        NavHost(
                            navController,
                            startDestination = main.pages.first(),
                            modifier = Modifier.hazeSource(hazeState)
                        ) {
                            with(padding) { main.pages.forEach { with(it) { builder() } } }

                            val subpagePadding = PaddingValues(start = paddingStart - insetsPaddingStart)
                            with(subpagePadding) { with(tools) { builtinTools.forEach { with(it.route) { builder() } } } }
                        }
                    }
                }
            }
        }
    }
}
