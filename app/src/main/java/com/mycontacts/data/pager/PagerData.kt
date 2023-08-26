package com.mycontacts.data.pager

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mycontacts.R

data class PagerData(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val description: Int
)

val pagerList = listOf(
    PagerData(R.drawable.icon_main, R.string.pagerTitle1, R.string.pagerDescription1),
    PagerData(R.drawable.icon_contact_list, R.string.pagerTitle2, R.string.pagerDescription2),
    PagerData(R.drawable.icon_editable_contact, R.string.pagerTitle3, R.string.pagerDescription3)
)