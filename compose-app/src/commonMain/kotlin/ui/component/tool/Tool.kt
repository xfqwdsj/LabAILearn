package top.ltfan.labailearn.ui.component.tool

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
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

                val width = if (constraints.hasBoundedWidth) constraints.maxWidth else content.width
                val height = if (constraints.hasBoundedHeight) constraints.maxHeight else content.height

                val background = subcompose("background") {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceContainerHighest)
                }.first().measure(Constraints.fixed(height * 3 / 4, height * 3 / 4))

                layout(width, height) {
                    background.place(width - background.width * 3 / 4, height - background.height * 3 / 4)
                    content.place(0, 0)
                }
            }
        }
    }
}
