package top.ltfan.labailearn.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.max

@Composable
fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    layoutType: NavigationSuiteType = NavigationSuiteType.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo()),
    topBar: @Composable (PaddingValues) -> Unit = {},
    navigationSuite: @Composable (layoutType: NavigationSuiteType) -> Unit = { _ -> },
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = WindowInsets.safeContent,
    content: @Composable (PaddingValues) -> Unit
) {
    with(LocalLayoutDirection.current) {
        SubcomposeLayout { constraints ->
            val navigationSuite = subcompose("navigationSuite") {
                Box {
                    AnimatedVisibility(
                        visible = layoutType != NavigationSuiteType.NavigationBar,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally(),
                    ) {
                        when (layoutType) {
                            NavigationSuiteType.NavigationBar, NavigationSuiteType.NavigationRail -> {
                                navigationSuite(NavigationSuiteType.NavigationRail)
                            }

                            NavigationSuiteType.NavigationDrawer -> {
                                navigationSuite(NavigationSuiteType.NavigationDrawer)
                            }
                        }
                    }
                }
            }.first().measure(constraints)

            val content = subcompose("content") {
                Scaffold(
                    modifier = modifier,
                    topBar = { topBar(PaddingValues(start = navigationSuite.width.toDp())) },
                    bottomBar = {
                        AnimatedVisibility(
                            visible = layoutType == NavigationSuiteType.NavigationBar,
                            enter = expandVertically(expandFrom = Alignment.Top),
                            exit = shrinkVertically(shrinkTowards = Alignment.Top)
                        ) { navigationSuite(NavigationSuiteType.NavigationBar) }
                    },
                    snackbarHost = snackbarHost,
                    floatingActionButton = floatingActionButton,
                    floatingActionButtonPosition = floatingActionButtonPosition,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    contentWindowInsets = contentWindowInsets,
                ) { contentPadding ->
                    val suiteWidth = navigationSuite.width.toDp()
                    val startPadding = max(contentPadding.calculateStartPadding(this@with), suiteWidth)

                    content(
                        PaddingValues(
                            start = startPadding,
                            top = contentPadding.calculateTopPadding(),
                            end = contentPadding.calculateEndPadding(this@with),
                            bottom = contentPadding.calculateBottomPadding()
                        )
                    )
                }
            }.first().measure(constraints)

            layout(constraints.maxWidth, constraints.maxHeight) {
                content.placeRelative(0, 0)
                navigationSuite.placeRelative(0, 0)
            }
        }
    }
}
