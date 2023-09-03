package com.mycontacts.presentation.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.R
import com.mycontacts.data.pager.pagerList
import com.mycontacts.presentation.pager.events.OnPagerEvent
import com.mycontacts.utils.Constants
import com.mycontacts.utils.Constants.second
import com.mycontacts.utils.Constants.zero
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Pager(
    onEvent: (OnPagerEvent) -> Unit
) {

    val pagerState = rememberPagerState(zero) {
        pagerList.size
    }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
        ) { currentPage ->
            PagerContent(
                pagerData = pagerList[currentPage]
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(dimensionResource(id = R.dimen._30dp))
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PagerIndicatorsSection(currentPage = pagerState.currentPage) { selectedIndicator ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(selectedIndicator)
                }
            }
            if (pagerState.currentPage == second) {
                Button(
                    onClick = {
                        onEvent(OnPagerEvent.OnButtonClick)
                },
                    modifier = Modifier.fillMaxWidth(Constants._05Float),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = dimensionResource(id = R.dimen._0dp),
                        pressedElevation = dimensionResource(id = R.dimen._10dp)
                    ),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pagerButton))
                ) {
                    Text(
                        text = stringResource(id = R.string.pagerButtonTitle),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W900)
                    )
                }
            }
        }
    }
}