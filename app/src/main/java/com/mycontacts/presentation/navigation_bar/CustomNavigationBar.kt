package com.mycontacts.presentation.navigation_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.mycontacts.utils.ScreenRoutes

data class NavigationBarSection(val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val title: String)

val navigationBarSections = listOf(
    NavigationBarSection(Icons.Default.Home, Icons.Outlined.Home, ScreenRoutes.Main.route),
    NavigationBarSection(Icons.Default.Settings, Icons.Outlined.Settings, ScreenRoutes.Settings.route)
)

@Composable
fun CustomNavigationBar(
    route: String,
    onNavigationBarSectionClick: (String) -> Unit
) {
    NavigationBar {
        navigationBarSections.forEach { navigationBarSection ->
            NavigationBarItem(
                selected = route == navigationBarSection.title,
                onClick = {
                    onNavigationBarSectionClick(navigationBarSection.title)
                },
                icon = {
                    Icon(
                        imageVector = if (route == navigationBarSection.title) navigationBarSection.selectedIcon else navigationBarSection.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = navigationBarSection.title,
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                alwaysShowLabel = route == navigationBarSection.title
            )
        }
    }
}