package top.ltfan.labailearn.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedVisibilityScope.BlurEnterExit(
    modifier: Modifier = Modifier, maxRadius: Dp = 8.dp, content: @Composable BoxScope.() -> Unit
) {
    val blurRadius by transition.animateDp { if (it != EnterExitState.Visible) maxRadius else 0.dp }

    Box(modifier.blur(blurRadius), content = content)
}
