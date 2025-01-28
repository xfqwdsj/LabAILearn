package top.ltfan.labailearn

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.chrisbanes.haze.*
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import labailearn.compose_app.generated.resources.Res
import labailearn.compose_app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import top.ltfan.labailearn.ui.AppViewModel
import top.ltfan.labailearn.ui.Route
import top.ltfan.labailearn.ui.component.AdaptiveScaffold
import top.ltfan.labailearn.ui.pages.main.Overview
import top.ltfan.labailearn.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class)
@Composable
fun App() {
    val navController = rememberNavController()
    with(viewModel { AppViewModel(navController) }) {
        AppTheme {
            val hazeState = remember { HazeState() }
            AdaptiveScaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(Res.string.app_name)) }, modifier = Modifier.hazeEffect(
                            hazeState, style = HazeMaterials.regular()
                        ) {
                            inputScale = HazeInputScale.Auto
                            progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                        }, colors = TopAppBarDefaults.largeTopAppBarColors(Color.Transparent)
                    )
                },
                navigationSuite = { layoutType, contentPadding ->
                    val currentPage = main.currentPage
                    NavigationSuite(
                        modifier = Modifier.hazeEffect(
                            hazeState, style = HazeMaterials.regular()
                        ).padding(contentPadding), layoutType = layoutType, colors = NavigationSuiteDefaults.colors(
                            navigationBarContainerColor = Color.Transparent,
                            navigationRailContainerColor = Color.Transparent,
                            navigationDrawerContainerColor = Color.Transparent
                        )
                    ) {
                        main.pages.forEach { route ->
                            item(
                                selected = currentPage == route,
                                onClick = { main.navigate(route) },
                                icon = { Icon(route.icon, contentDescription = stringResource(route.label)) },
                                label = { Text(stringResource(route.label)) })
                        }
                    }
                },
            ) { padding ->
                NavHost(
                    navController, startDestination = Route.Main.Overview, modifier = Modifier.hazeSource(hazeState)
                ) {
                    composable<Route.Main.Overview> {
                        Overview(padding)
                    }
                    composable<Route.Main.Tools> {
                        Text("Tools")
                    }
                    composable<Route.Main.Settings> {
                        Text("Settings")
                    }
                }
            }
        }
    }
}
