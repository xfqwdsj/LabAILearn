package top.ltfan.labailearn.data

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import top.ltfan.labailearn.ui.Route
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Tool {
    val label: StringResource
    val description: StringResource
    val category: ToolCategory
    val tags: List<String>
    val icon: ImageVector

    @OptIn(ExperimentalUuidApi::class)
    val routeBuilder: (Uuid) -> Route.Main.Tools.Tool
}

sealed interface ToolCategory {
    val label: StringResource
    val icon: ImageVector
}
