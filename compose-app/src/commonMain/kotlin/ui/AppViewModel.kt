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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

    @OptIn(ExperimentalUuidApi::class)
    val tools = object : Tools {
        val builtinTools = listOf<Tool>()

        val builtinToolsMap = builtinTools.map { it to it.routeBuilder(Uuid.random()) }.associateBy { it.second.uuid }

        override val tools get() = builtinToolsMap

        override val Uuid.tool: Tool
            get() = tools[this]?.first ?: throw IllegalArgumentException("Tool with Uuid $Uuid not found")

        override val Uuid.route
            get() = tools[this]?.second ?: throw IllegalArgumentException("Tool with Uuid $Uuid not found")
    }

    val NavController.currentPageAsState: State<Route>
        @Composable inline get() {
            val navBackStackEntry = currentBackStackEntryAsState()
            return derivedStateOf { navBackStackEntry.value?.currentPage ?: pages.first() }
        }

    val NavBackStackEntry.currentPage: Route?
        get() = pages.find { route -> destination.hierarchy.any { it.hasRoute(route::class) } }

    @OptIn(ExperimentalUuidApi::class)
    val pages = main.pages + tools.tools.values.map { it.second }

    interface Main {
        val pages: List<Route.Main>
        context(navController: NavController) fun navigate(destination: Route.Main)
    }

    interface Tools {
        @OptIn(ExperimentalUuidApi::class)
        val Uuid.tool: Tool

        @OptIn(ExperimentalUuidApi::class)
        val Uuid.route: Route.Main.Tools.Tool

        @OptIn(ExperimentalUuidApi::class)
        val tools: Map<Uuid, Pair<Tool, Route.Main.Tools.Tool>>
    }
}
