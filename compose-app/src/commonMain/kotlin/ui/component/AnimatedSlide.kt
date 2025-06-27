package top.ltfan.labailearn.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMaxBy
import androidx.compose.ui.util.fastRoundToInt
import kotlinx.coroutines.launch

/**
 * AnimatedSlide animates sliding content in and out, while preserving the content's original size.
 *
 * @param active Whether the content is currently active (slided in).
 * @param modifier The modifier to be applied to the container.
 * @param slideDirection The direction from which the content will slide in when becoming active and to which it will
 * slide out when becoming inactive.
 * @param animationSpec The [AnimationSpec] to be used for the sliding animation.
 * @param background An optional composable that will be drawn behind the content, useful for background effects.
 * @param content The content to be displayed within the sliding container.
 */
@Composable
fun AnimatedSlide(
    active: Boolean,
    modifier: Modifier = Modifier,
    slideDirection: SlideDirection = SlideDirection.TopStart,
    animationSpec: AnimationSpec<Float> = spring(),
    background: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AnimatedSlide(
        active = active,
        modifier = modifier,
        slideFrom = slideDirection,
        slideTo = slideDirection,
        animationSpec = animationSpec,
        background = background,
        content = content
    )
}

/**
 * AnimatedSlide animates sliding content in and out, while preserving the content's original size.
 *
 * @param active Whether the content is currently active (slided in).
 * @param modifier The modifier to be applied to the container.
 * @param slideFrom The direction from which the content will slide in when becoming active.
 * @param slideTo The direction to which the content will slide out when becoming inactive.
 * @param animationSpec The [AnimationSpec] to be used for the sliding animation.
 * @param background An optional composable that will be drawn behind the content, useful for background effects.
 * @param content The content to be displayed within the sliding container.
 */
@Composable
fun AnimatedSlide(
    active: Boolean,
    modifier: Modifier = Modifier,
    slideFrom: SlideDirection = SlideDirection.TopStart,
    slideTo: SlideDirection = SlideDirection.TopStart,
    animationSpec: AnimationSpec<Float> = spring(),
    background: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var size: Size? by remember { mutableStateOf(null) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val layoutDirection = LocalLayoutDirection.current

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(active, slideFrom, slideTo) {
        val size = size
        if (size == null) return@LaunchedEffect

        if (active) {
            offsetX.animateTo(0f, animationSpec = animationSpec)
            offsetY.animateTo(0f, animationSpec = animationSpec)
        } else {
            slideTo.updateOffset(
                width = size.width,
                height = size.height,
                updateOffsetX = { offsetX.animateTo(it, animationSpec) },
                updateOffsetY = { offsetY.animateTo(it, animationSpec) },
                layoutDirection = layoutDirection
            )
            slideFrom.updateOffset(
                width = size.width,
                height = size.height,
                updateOffsetX = offsetX::snapTo,
                updateOffsetY = offsetY::snapTo,
                layoutDirection = layoutDirection
            )
        }
    }

    SubcomposeLayout(modifier) { constraints ->
        val contentPlaceables = subcompose("content", content).map { it.measure(constraints) }

        val width = contentPlaceables.fastMaxBy { it.width }?.width ?: 0
        val height = contentPlaceables.fastMaxBy { it.height }?.height ?: 0

        if (size == null || size!!.width != width.toFloat() || size!!.height != height.toFloat()) {
            val newSize = Size(width.toFloat(), height.toFloat())
            size = newSize
            if (!active) {
                coroutineScope.launch {
                    slideFrom.updateOffset(
                        width = newSize.width,
                        height = newSize.height,
                        updateOffsetX = offsetX::snapTo,
                        updateOffsetY = offsetY::snapTo,
                        layoutDirection = layoutDirection
                    )
                }
            }
        }

        val backgroundPlaceables = background?.let {
            subcompose("background", background).map { it.measure(Constraints.fixed(width, height)) }
        }

        layout(width, height) {
            backgroundPlaceables?.fastForEach { it.place(0, 0) }
            contentPlaceables.fastForEach {
                it.place(offsetX.value.fastRoundToInt(), offsetY.value.fastRoundToInt())
            }
        }
    }
}

/**
 * Enum representing the possible directions for sliding animations.
 */
enum class SlideDirection {
    Start, Top, End, Bottom, TopStart, TopEnd, BottomStart, BottomEnd;

    /**
     * Updates the offset values when the content slided out based on the specified width, height, and layout direction.
     */
    suspend inline fun updateOffset(
        width: Float,
        height: Float,
        updateOffsetX: suspend (Float) -> Unit,
        updateOffsetY: suspend (Float) -> Unit,
        layoutDirection: LayoutDirection
    ) {
        val factor = when (layoutDirection) {
            LayoutDirection.Ltr -> 1f
            LayoutDirection.Rtl -> -1f
        }

        when (this) {
            Start -> updateOffsetX(-width * factor)
            Top -> updateOffsetY(-height)
            End -> updateOffsetX(width * factor)
            Bottom -> updateOffsetY(height)
            TopStart -> {
                updateOffsetX(-width * factor)
                updateOffsetY(-height)
            }

            TopEnd -> {
                updateOffsetX(width * factor)
                updateOffsetY(-height)
            }

            BottomStart -> {
                updateOffsetX(-width * factor)
                updateOffsetY(height)
            }

            BottomEnd -> {
                updateOffsetX(width * factor)
                updateOffsetY(height)
            }
        }
    }
}
