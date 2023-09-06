package com.mycontacts.presentation.pager.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mycontacts.R
import com.mycontacts.data.pager.PagerData
import com.mycontacts.utils.Constants._04Float
import com.mycontacts.utils.Constants._18sp
import com.mycontacts.utils.Constants._06Float
import com.mycontacts.utils.Constants.pagerImageSmallSizeInPixels
import com.mycontacts.utils.Constants._22sp

@Composable
fun PagerContent(
    pagerData: PagerData
) {
    val context = LocalContext.current
    val resources = context.resources

    val (pagerImageWidthInPixels, pagerImageHeightInPixels) = getPagerImageSize(resources = resources, drawableResId = pagerData.image)
    val pagerImageContentScale =
        if (pagerImageWidthInPixels == pagerImageSmallSizeInPixels && pagerImageHeightInPixels == pagerImageSmallSizeInPixels) ContentScale.Fit
        else ContentScale.Inside

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.pager)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .weight(_06Float)
                .padding(dimensionResource(id = R.dimen._10dp)),
            painter = painterResource(id = pagerData.image),
            contentDescription = null,
            contentScale = pagerImageContentScale
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(_04Float),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen._0dp)),
            shape = RoundedCornerShape(
                topStart = dimensionResource(id = R.dimen._50dp),
                topEnd = dimensionResource(id = R.dimen._50dp)
            ),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cardPager))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen._10dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._10dp), Alignment.Top)
            ) {
                Text(
                    text = stringResource(id = pagerData.title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = _22sp,
                        fontWeight = FontWeight.W500,
                        fontFamily = FontFamily.SansSerif
                    )
                )
                Text(
                    text = stringResource(id = pagerData.description),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = _18sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}