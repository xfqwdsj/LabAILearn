package top.ltfan.labailearn.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import top.ltfan.labailearn.data.Tool

class AppViewModel : ViewModel() {
    val main = object : Main {
        override val pages = listOf<Route.Main>(
            Route.Main.Overview(), Route.Main.Tools(), Route.Main.Settings()
        ) // 不加 <Route.Main> 会导致 WasmJs编译失败

        context(navController: NavController) override fun navigate(destination: Route.Main) {
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

    val tools = object : Tools {
        override val builtinTools: List<Tool> = listOf()

        override val routes: Map<Tool, Route> = builtinTools.associateWith { it.routeBuilder() }

        override val Tool.route: Route
            get() = routes[this] ?: error("Tool $this does not have a route defined")
    }

    val NavController.currentPageAsState: State<Route>
        @Composable inline get() {
            val navBackStackEntry = currentBackStackEntryAsState()
            return derivedStateOf { navBackStackEntry.value?.currentPage ?: pages.first() }
        }

    val NavBackStackEntry.currentPage: Route?
        get() = pages.find { route -> destination.hierarchy.any { it.hasRoute(route::class) } }

    val pages = main.pages + tools.routes.values

    interface Main {
        val pages: List<Route.Main>
        context(navController: NavController) fun navigate(destination: Route.Main)
    }

    interface Tools {
        val builtinTools: List<Tool>
        val routes: Map<Tool, Route>
        val Tool.route: Route
    }
}
