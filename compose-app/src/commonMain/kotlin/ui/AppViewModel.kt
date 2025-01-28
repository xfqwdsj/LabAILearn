package top.ltfan.labailearn.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

class AppViewModel(val navController: NavHostController) : ViewModel() {
    interface Main {
        val pages: List<Route.Main>

        val currentPage: Route.Main @Composable get

        fun navigate(destination: Route.Main)
    }

    val main = object : Main {
        override val pages = listOf<Route.Main>(
            Route.Main.Overview, Route.Main.Tools, Route.Main.Settings
        ) // 不加 <Route.Main> 会导致 WasmJs编译失败

        override val currentPage: Route.Main
            @Composable get() {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                return pages.find { route -> navBackStackEntry?.destination?.hierarchy?.any { it.hasRoute(route::class) } == true }
                    ?: Route.Main.Overview
            }

        override fun navigate(destination: Route.Main) {
            navController.navigate(destination) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo<Route.Main.Overview> {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }
    }
}
