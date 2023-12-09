package com.mycontacts.presentation.top_app_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.utils.Constants._22sp
import com.mycontacts.utils.ScreenRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    route: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigationIconClick: () -> Unit
) {
    if (route != ScreenRoutes.ContactOperations.route) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = route,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = _22sp,
                        fontWeight = FontWeight.W900
                    )
                )
            },
            navigationIcon = {
                if (route != ScreenRoutes.Main.route) {
                    IconButton(
                        onClick = { onNavigationIconClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = route,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = _22sp,
                        fontWeight = FontWeight.W900
                    )
                )
            },
            navigationIcon = {
                if (route != ScreenRoutes.Main.route) {
                    IconButton(
                        onClick = { onNavigationIconClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            }
        )
    }

}