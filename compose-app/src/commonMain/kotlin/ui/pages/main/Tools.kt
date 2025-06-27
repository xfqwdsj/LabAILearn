package top.ltfan.labailearn.ui.pages.main

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.ltfan.labailearn.data.RouteTransitionType
import top.ltfan.labailearn.data.transitionKeyOf
import top.ltfan.labailearn.ui.AppViewModel
import top.ltfan.labailearn.ui.component.tool.ToolCard

context(navController: NavController, sharedTransitionScope: SharedTransitionScope, animatedVisibilityScope: AnimatedVisibilityScope, contentPadding: PaddingValues) @OptIn(
    ExperimentalSharedTransitionApi::class
)
@Composable
fun AppViewModel.ToolsPage() {
    val layoutDirection = LocalLayoutDirection.current
    val additionalPadding = 16.dp
    val contentPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection) + additionalPadding,
        end = contentPadding.calculateEndPadding(layoutDirection) + additionalPadding,
        top = contentPadding.calculateTopPadding() + additionalPadding,
        bottom = contentPadding.calculateBottomPadding() + additionalPadding
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 256.dp),
        contentPadding = contentPadding,
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        with(tools) {
            items(builtinTools, { it.hashCode() }) { tool ->
                with(sharedTransitionScope) {
                    ToolCard(
                        tool,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                tool.route.transitionKeyOf(RouteTransitionType.Container)
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        ),
                        titleModifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                tool.route.transitionKeyOf(RouteTransitionType.Title)
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                        onToolClick = { navController.navigate(tool.route) },
                        onCategoryClick = {},
                    )
                }
            }
        }
    }
}
