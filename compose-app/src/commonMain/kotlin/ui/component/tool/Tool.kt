package top.ltfan.labailearn.ui.component.tool

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import top.ltfan.labailearn.data.Tool

@Composable
fun ToolCard(
    tool: Tool,
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    onToolClick: () -> Unit,
    onCategoryClick: () -> Unit
) {
    with(tool) {
        OutlinedCard(onClick = onToolClick, modifier = modifier) {
            SubcomposeLayout { constraints ->
                val background = subcompose("background") {
                    Icon(
                        icon, contentDescription = null,
                        modifier = Modifier.size(128.dp).clipToBounds(),
                        tint = MaterialTheme.colorScheme.surfaceContainerHighest,
                    )
                }.first().measure(constraints)

                val content = subcompose("content") {
                    Column(Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(16.dp))
                        SuggestionChip(
                            onClick = onCategoryClick,
                            label = { Text(stringResource(tool.category.label)) },
                            modifier = Modifier.padding(horizontal = 24.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Column(Modifier.padding(horizontal = 32.dp)) {
                            Text(stringResource(label), titleModifier, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(16.dp))
                            Text(stringResource(description))
                        }
                        Spacer(Modifier.height(32.dp))
                    }
                }.first().measure(constraints)

                layout(content.width, content.height) {
                    background.place(
                        content.width - background.width + 32.dp.roundToPx(),
                        content.height - background.height + 32.dp.roundToPx()
                    )
                    content.place(0, 0)
                }
            }
        }
    }
}
