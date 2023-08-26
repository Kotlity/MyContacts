package com.mycontacts.presentation.pager

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.mycontacts.R
import com.mycontacts.utils.Constants.third

@Composable
fun PagerIndicatorsSection(
    currentPage: Int,
    onIndicatorClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.height(dimensionResource(id = R.dimen._40dp)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(third) { currentIndicator ->

            val indicatorTransition = updateTransition(targetState = currentPage == currentIndicator, label = null)

            val animatedIndicatorSize by indicatorTransition.animateDp(
                label = "") { isTheCurrentIndicator ->
                when (isTheCurrentIndicator) {
                    true -> dimensionResource(id = R.dimen._30dp)
                    false -> dimensionResource(id = R.dimen._20dp)
                }
            }
            val animatedIndicatorColor by indicatorTransition.animateColor(
                label = ""
            ) { isTheCurrentIndicator ->
                when (isTheCurrentIndicator) {
                    true -> colorResource(id = R.color.currentIndicator)
                    false -> colorResource(id = R.color.indicator)
                }
            }

            Box(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen._2dp))
                    .clip(CircleShape)
                    .size(animatedIndicatorSize)
                    .background(animatedIndicatorColor)
                    .clickable {
                        if (currentIndicator != currentPage) onIndicatorClick(currentIndicator)
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPagerIndicatorsSection() {
    PagerIndicatorsSection(currentPage = 1) { }
}