package top.ltfan.labailearn.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun NavHostController.NavHost(
    startDestination: Any, modifier: Modifier = Modifier, builder: NavGraphBuilder.() -> Unit
) {
    NavHost(navController = this, startDestination = startDestination, modifier = modifier, builder = builder)
}
