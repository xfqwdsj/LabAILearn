package top.ltfan.labailearn.ui.pages.main

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import top.ltfan.labailearn.ui.AppViewModel

context(sharedTransitionScope: SharedTransitionScope, contentPadding: PaddingValues)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppViewModel.OverviewPage() {
    with(LocalLayoutDirection.current) {
        Column(
            Modifier.padding(
                start = contentPadding.calculateStartPadding(this),
                end = contentPadding.calculateEndPadding(this),
            ).verticalScroll(
                rememberScrollState()
            ).padding(top = contentPadding.calculateTopPadding(), bottom = contentPadding.calculateBottomPadding())
        ) {
            Text(
                "Overview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview\nOverview",
                color = Color.Red
            )
        }
    }
}
