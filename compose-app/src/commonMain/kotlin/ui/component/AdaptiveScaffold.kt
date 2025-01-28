package top.ltfan.labailearn.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    layoutType: NavigationSuiteType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
        currentWindowAdaptiveInfo()
    ),
    topBar: @Composable () -> Unit = {},
    navigationSuite: @Composable (layoutType: NavigationSuiteType, contentPadding: PaddingValues) -> Unit = { _, _ -> },
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = {
            AnimatedVisibility(
                visible = layoutType == NavigationSuiteType.NavigationBar,
                enter = expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top)
            ) { navigationSuite(NavigationSuiteType.NavigationBar, PaddingValues()) }
        },
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets,
    ) {
        Row {
            AnimatedVisibility(
                visible = layoutType != NavigationSuiteType.NavigationBar,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                with(LocalLayoutDirection.current) {
                    when (layoutType) {
                        NavigationSuiteType.NavigationBar, NavigationSuiteType.NavigationRail -> {
                            navigationSuite(
                                NavigationSuiteType.NavigationRail, PaddingValues(
                                    start = it.calculateStartPadding(this),
                                    top = it.calculateTopPadding(),
                                    bottom = it.calculateBottomPadding()
                                )
                            )
                        }
                        NavigationSuiteType.NavigationDrawer -> {
                            navigationSuite(
                                NavigationSuiteType.NavigationDrawer, PaddingValues(
                                    start = it.calculateStartPadding(this),
                                    top = it.calculateTopPadding(),
                                    bottom = it.calculateBottomPadding()
                                )
                            )
                        }
                    }
                }
            }
            with(LocalLayoutDirection.current) {
                content(
                    PaddingValues(
                        top = it.calculateTopPadding(),
                        end = it.calculateEndPadding(this),
                        bottom = it.calculateBottomPadding()
                    )
                )
            }
        }
    }
}
