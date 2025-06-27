package top.ltfan.labailearn.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

context(hazeState: HazeState)
fun Modifier.hazeSource(zIndex: Float = 0f, key: Any? = null) = hazeSource(hazeState, zIndex, key)

context(hazeState: HazeState)
@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class)
@Composable
fun Modifier.appBarHazeEffect(type: HazeAppBarType) = hazeEffect(hazeState, style = HazeMaterials.regular()) {
    inputScale = HazeInputScale.Auto
    progressive = HazeProgressive.verticalGradient(
        startIntensity = if (type == HazeAppBarType.Top) 1f else 0f,
        endIntensity = if (type == HazeAppBarType.Top) 0f else 1f,
    )
}

enum class HazeAppBarType { Top, Bottom }
