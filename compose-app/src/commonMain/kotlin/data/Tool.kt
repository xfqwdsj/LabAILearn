package top.ltfan.labailearn.data

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import top.ltfan.labailearn.ui.Route

interface Tool {
    val label: StringResource
    val description: StringResource
    val category: ToolCategory
    val tags: List<String>
    val icon: ImageVector
    val routeBuilder: () -> Route
}

sealed interface ToolCategory {
    val label: StringResource
    val icon: ImageVector
}
