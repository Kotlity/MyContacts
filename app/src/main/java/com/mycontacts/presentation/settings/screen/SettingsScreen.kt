package com.mycontacts.presentation.settings.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.R
import com.mycontacts.presentation.initial.events.InitialEvent
import com.mycontacts.presentation.initial.viewmodels.InitialViewModel
import com.mycontacts.presentation.settings.composables.CustomLanguageDropdownMenu
import com.mycontacts.presentation.settings.composables.CustomUiModeSwitcher
import com.mycontacts.utils.Constants._04Float
import com.mycontacts.utils.Constants._22sp
import com.mycontacts.utils.sharedViewModel

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val initialViewModel: InitialViewModel = context.sharedViewModel()

    val isSystemInDarkUiMode = initialViewModel.isDarkUiMode
    val currentLanguageCode = initialViewModel.currentLanguageCode
    val isExpandedLanguageDropdownMenu = initialViewModel.isExpandedLanguageDropdownMenu

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen._7dp)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._10dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.uiThemeTitle),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = _22sp,
                    fontWeight = FontWeight.Bold
                )
            )
            CustomUiModeSwitcher(
                isSystemInDarkUiMode = isSystemInDarkUiMode,
                onToggleSwitch = { isInDarkUiMode ->
                    initialViewModel.onEvent(InitialEvent.UpdateIsDarkUiModePreferences(isInDarkUiMode))
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.applicationLanguageTitle),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = _22sp,
                    fontWeight = FontWeight.Bold
                )
            )
            CustomLanguageDropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(_04Float),
                isExpanded = isExpandedLanguageDropdownMenu,
                onExpandedChange = { isExpanded ->
                    initialViewModel.onEvent(InitialEvent.ChangeLanguageDropdownMenuExpandedState(isExpanded))
                },
                onLanguageChange = { languageCode ->
                    initialViewModel.onEvent(InitialEvent.ChangeAppLanguage(languageCode))
                },
                languageIcon = if (currentLanguageCode == stringResource(id = R.string.usaLanguageCode)) R.drawable.usa else R.drawable.ukraine
            )
        }
    }
}