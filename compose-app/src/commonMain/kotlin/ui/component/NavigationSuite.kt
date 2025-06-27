/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.ltfan.labailearn.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * The default Material navigation component according to the current [NavigationSuiteType] to be
 * used with the [NavigationSuiteScaffold].
 *
 * For specifics about each navigation component, see [NavigationBar], [NavigationRail], and
 * [PermanentDrawerSheet].
 *
 * @param modifier the [Modifier] to be applied to the navigation component
 * @param layoutType the current [NavigationSuiteType] of the [NavigationSuiteScaffold]. Defaults to
 * [NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo]
 * @param colors [NavigationSuiteColors] that will be used to determine the container (background)
 * color of the navigation component and the preferred color for content inside the navigation
 * component
 * @param containers [NavigationSuiteContainers]
 * @param tonalElevations [NavigationSuiteTonalElevations] that will be used to determine the
 * tonal elevation of the navigation component
 * @param windowInsets [NavigationSuiteWindowInsets] that will be used to determine the
 * window insets for the navigation component
 * @param content the content inside the current navigation component, typically
 * [NavigationSuiteScope.item]s
 */
@Composable
fun NavigationSuite(
    modifier: Modifier = Modifier,
    layoutType: NavigationSuiteType = NavigationSuiteType.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo()),
    containers: NavigationSuiteContainers = NavigationSuiteDefaults.containers(),
    colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
    tonalElevations: NavigationSuiteTonalElevations = NavigationSuiteDefaults.tonalElevations(),
    windowInsets: NavigationSuiteWindowInsets = NavigationSuiteDefaults.windowInsets(),
    content: NavigationSuiteScope.() -> Unit
) {
    val scope by rememberStateOfItems(content)
    // Define defaultItemColors here since we can't set NavigationSuiteDefaults.itemColors() as a
    // default for the colors param of the NavigationSuiteScope.item non-composable function.
    val defaultItemColors = NavigationSuiteDefaults.itemColors()

    when (layoutType) {
        NavigationSuiteType.NavigationBar -> {
            containers.navigationBarContainer {
                NavigationBar(
                    modifier = modifier,
                    containerColor = colors.navigationBarContainerColor,
                    contentColor = colors.navigationBarContentColor,
                    tonalElevation = tonalElevations.navigationBarTonalElevation,
                    windowInsets = windowInsets.navigationBarWindowInsets
                ) {
                    scope.itemList.forEach {
                        NavigationBarItem(
                            modifier = it.modifier,
                            selected = it.selected,
                            onClick = it.onClick,
                            icon = { NavigationItemIcon(icon = it.icon, badge = it.badge) },
                            enabled = it.enabled,
                            label = it.label,
                            alwaysShowLabel = it.alwaysShowLabel,
                            colors = it.colors?.navigationBarItemColors ?: defaultItemColors.navigationBarItemColors,
                            interactionSource = it.interactionSource
                        )
                    }
                }
            }
        }

        NavigationSuiteType.NavigationRail -> {
            containers.navigationRailContainer {
                NavigationRail(
                    modifier = modifier,
                    containerColor = colors.navigationRailContainerColor,
                    contentColor = colors.navigationRailContentColor,
                    windowInsets = windowInsets.navigationRailWindowInsets
                ) {
                    scope.itemList.forEach {
                        NavigationRailItem(
                            modifier = it.modifier,
                            selected = it.selected,
                            onClick = it.onClick,
                            icon = { NavigationItemIcon(icon = it.icon, badge = it.badge) },
                            enabled = it.enabled,
                            label = it.label,
                            alwaysShowLabel = it.alwaysShowLabel,
                            colors = it.colors?.navigationRailItemColors ?: defaultItemColors.navigationRailItemColors,
                            interactionSource = it.interactionSource
                        )
                    }
                }
            }
        }

        NavigationSuiteType.NavigationDrawer -> {
            containers.navigationDrawerContainer {
                PermanentDrawerSheet(
                    modifier = modifier,
                    drawerContainerColor = colors.navigationDrawerContainerColor,
                    drawerContentColor = colors.navigationDrawerContentColor,
                    drawerTonalElevation = tonalElevations.navigationDrawerTonalElevation,
                    windowInsets = windowInsets.navigationDrawerWindowInsets
                ) {
                    scope.itemList.forEach {
                        NavigationDrawerItem(
                            modifier = it.modifier,
                            selected = it.selected,
                            onClick = it.onClick,
                            icon = it.icon,
                            badge = it.badge,
                            label = { it.label?.invoke() ?: Text("") },
                            colors = it.colors?.navigationDrawerItemColors
                                ?: defaultItemColors.navigationDrawerItemColors,
                            interactionSource = it.interactionSource
                        )
                    }
                }
            }
        }

        NavigationSuiteType.None -> { /* Do nothing. */
        }
    }
}

private interface NavigationSuiteItemProvider {
    val itemsCount: Int
    val itemList: MutableVector<NavigationSuiteItem>
}

private class NavigationSuiteItem(
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: @Composable (() -> Unit)?,
    val alwaysShowLabel: Boolean,
    val badge: (@Composable () -> Unit)?,
    val colors: NavigationSuiteItemColors?,
    val interactionSource: MutableInteractionSource
)

/** The scope associated with the [NavigationSuiteScope]. */
sealed interface NavigationSuiteScope {

    /**
     * This function sets the parameters of the default Material navigation item to be used with the
     * Navigation Suite Scaffold. The item is called in [NavigationSuite], according to the
     * current [NavigationSuiteType].
     *
     * For specifics about each item component, see [NavigationBarItem], [NavigationRailItem], and
     * [NavigationDrawerItem].
     *
     * @param selected whether this item is selected
     * @param onClick called when this item is clicked
     * @param icon icon for this item, typically an [androidx.compose.material3.Icon]
     * @param modifier the [Modifier] to be applied to this item
     * @param enabled controls the enabled state of this item. When `false`, this component will not
     * respond to user input, and it will appear visually disabled and disabled to accessibility
     * services. Note: as of now, for [NavigationDrawerItem], this is always `true`.
     * @param label the text label for this item
     * @param alwaysShowLabel whether to always show the label for this item. If `false`, the label
     * will only be shown when this item is selected. Note: for [NavigationDrawerItem] this is
     * always `true`
     * @param badge optional badge to show on this item
     * @param colors [NavigationSuiteItemColors] that will be used to resolve the colors used for
     * this item in different states. If null, [NavigationSuiteDefaults.itemColors] will be used.
     * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
     * emitting [androidx.compose.foundation.interaction.Interaction]s for this item. You can use this to change the item's appearance
     * or preview the item in different states. Note that if `null` is provided, interactions will
     * still happen internally.
     */
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        label: @Composable (() -> Unit)? = null,
        alwaysShowLabel: Boolean = true,
        badge: (@Composable () -> Unit)? = null,
        colors: NavigationSuiteItemColors? = null,
        interactionSource: MutableInteractionSource? = null
    )
}

private class NavigationSuiteScopeImpl : NavigationSuiteScope, NavigationSuiteItemProvider {
    override fun item(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier,
        enabled: Boolean,
        label: @Composable (() -> Unit)?,
        alwaysShowLabel: Boolean,
        badge: (@Composable () -> Unit)?,
        colors: NavigationSuiteItemColors?,
        interactionSource: MutableInteractionSource?
    ) {
        itemList.add(
            NavigationSuiteItem(
                selected = selected,
                onClick = onClick,
                icon = icon,
                modifier = modifier,
                enabled = enabled,
                label = label,
                alwaysShowLabel = alwaysShowLabel,
                badge = badge,
                colors = colors,
                interactionSource = interactionSource ?: MutableInteractionSource()
            )
        )
    }

    override val itemList: MutableVector<NavigationSuiteItem> = mutableVectorOf()

    override val itemsCount: Int
        get() = itemList.size
}

@Composable
private fun rememberStateOfItems(
    content: NavigationSuiteScope.() -> Unit
): State<NavigationSuiteItemProvider> {
    val latestContent = rememberUpdatedState(content)
    return remember {
        derivedStateOf { NavigationSuiteScopeImpl().apply(latestContent.value) }
    }
}

@Composable
private fun NavigationItemIcon(
    icon: @Composable () -> Unit,
    badge: (@Composable () -> Unit)? = null,
) {
    if (badge != null) {
        BadgedBox(badge = { badge.invoke() }) {
            icon()
        }
    } else {
        icon()
    }
}

data class NavigationSuiteContainers(
    val navigationBarContainer: @Composable ((@Composable () -> Unit) -> Unit),
    val navigationRailContainer: @Composable ((@Composable () -> Unit) -> Unit),
    val navigationDrawerContainer: @Composable ((@Composable () -> Unit) -> Unit)
)

@Suppress("UnusedReceiverParameter")
fun NavigationSuiteDefaults.containers(
    navigationBarContainer: @Composable ((@Composable () -> Unit) -> Unit) = { it() },
    navigationRailContainer: @Composable ((@Composable () -> Unit) -> Unit) = { it() },
    navigationDrawerContainer: @Composable ((@Composable () -> Unit) -> Unit) = { it() }
): NavigationSuiteContainers {
    return NavigationSuiteContainers(
        navigationBarContainer = navigationBarContainer,
        navigationRailContainer = navigationRailContainer,
        navigationDrawerContainer = navigationDrawerContainer
    )
}

data class NavigationSuiteTonalElevations(
    val navigationBarTonalElevation: Dp, val navigationDrawerTonalElevation: Dp
)

@Suppress("UnusedReceiverParameter")
fun NavigationSuiteDefaults.tonalElevations(
    navigationBarTonalElevation: Dp = NavigationBarDefaults.Elevation,
    navigationDrawerTonalElevation: Dp = DrawerDefaults.PermanentDrawerElevation
): NavigationSuiteTonalElevations {
    return NavigationSuiteTonalElevations(
        navigationBarTonalElevation = navigationBarTonalElevation,
        navigationDrawerTonalElevation = navigationDrawerTonalElevation
    )
}

data class NavigationSuiteWindowInsets(
    val navigationBarWindowInsets: WindowInsets,
    val navigationRailWindowInsets: WindowInsets,
    val navigationDrawerWindowInsets: WindowInsets
)

@Composable
fun NavigationSuiteDefaults.windowInsetsWithDefaultSides(all: WindowInsets): NavigationSuiteWindowInsets {
    return windowInsetsWithDefaultSides(all, all, all)
}

@Composable
fun NavigationSuiteDefaults.windowInsetsWithDefaultSides(
    navigationBarWindowInsets: WindowInsets = WindowInsets.safeContent.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
    navigationRailWindowInsets: WindowInsets = WindowInsets.safeContent.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
    navigationDrawerWindowInsets: WindowInsets = WindowInsets.safeContent.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)
): NavigationSuiteWindowInsets {
    return windowInsets(
        navigationBarWindowInsets = navigationBarWindowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
        navigationRailWindowInsets = navigationRailWindowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
        navigationDrawerWindowInsets = navigationDrawerWindowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    )
}

@Suppress("UnusedReceiverParameter")
@Composable
fun NavigationSuiteDefaults.windowInsets(
    navigationBarWindowInsets: WindowInsets = WindowInsets.safeContent.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
    navigationRailWindowInsets: WindowInsets = WindowInsets.safeContent.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
    navigationDrawerWindowInsets: WindowInsets = WindowInsets.safeContent.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)
): NavigationSuiteWindowInsets {
    return NavigationSuiteWindowInsets(
        navigationBarWindowInsets = navigationBarWindowInsets,
        navigationRailWindowInsets = navigationRailWindowInsets,
        navigationDrawerWindowInsets = navigationDrawerWindowInsets
    )
}

/**
 * Returns the expected [NavigationSuiteType] according to the provided [WindowAdaptiveInfo].
 * Usually used with the [NavigationSuiteScaffold] and related APIs.
 *
 * @param adaptiveInfo the provided [WindowAdaptiveInfo]
 * @see NavigationSuiteScaffold
 */
fun NavigationSuiteType.Companion.calculateFromAdaptiveInfo(adaptiveInfo: WindowAdaptiveInfo): NavigationSuiteType {
    return with(adaptiveInfo) {
        when {
            windowPosture.isTabletop || windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT -> {
                NavigationSuiteType.NavigationBar
            }

            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM -> {
                NavigationSuiteType.NavigationRail
            }

            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED -> {
                NavigationSuiteType.NavigationDrawer
            }

            else -> {
                NavigationSuiteType.NavigationBar
            }
        }
    }
}
