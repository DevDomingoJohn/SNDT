package com.domin.sndt.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.domin.sndt.InfoScreen
import com.domin.sndt.ScanScreen
import com.domin.sndt.ToolsScreen
import kotlinx.coroutines.launch

data class NavigationDrawerState(
    val drawerState: DrawerState = DrawerState(initialValue = DrawerValue.Closed),
    var selectedItem: MutableState<Int> = mutableIntStateOf(0)
)

data class NavigationItem(
    val icon: ImageVector,
    val title: String,
    val route: Any
)

@Composable
fun NavigationDrawer(
    state: NavigationDrawerState = NavigationDrawerState(),
    content: @Composable (NavigationDrawerState, NavHostController) -> Unit
) {
    val navController = rememberNavController()
    val coroutine = rememberCoroutineScope()

    val items = listOf(
        NavigationItem(
            title = "Information",
            icon = Icons.Outlined.Info,
            route = InfoScreen,
        ),
        NavigationItem(
            title = "Lan Scan",
            icon = Icons.Outlined.Search,
            route = ScanScreen
        ),
        NavigationItem(
            title = "Tools",
            icon = Icons.Outlined.Build,
            route = ToolsScreen
        )
    )
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Esterd",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp)
                )
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {Text(item.title)},
                        icon = { Icon(item.icon,item.title) },
                        selected = index == state.selectedItem.value,
                        onClick = {
                            state.selectedItem.value = index
                            coroutine.launch {
                                state.drawerState.close()
                            }
                            navController.navigate(item.route) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = state.drawerState
    ) {
        content(state,navController)
    }
}
