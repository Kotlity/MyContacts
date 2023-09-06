package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.R
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.Constants._15sp
import com.mycontacts.utils.Constants._17sp
import com.mycontacts.utils.Constants._18sp
import com.mycontacts.utils.Constants._20sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactItem(
    contactInfo: ContactInfo,
    onContactClick: (ContactInfo) -> Unit
) {
    Card(
        onClick = {
            onContactClick(contactInfo)
        },
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen._10dp)),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen._0dp),
            pressedElevation = dimensionResource(id = R.dimen._10dp)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen._10dp),
                    top = dimensionResource(id = R.dimen._10dp)
                )
        ) {
            if (contactInfo.photo != null) {
                Image(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen._80dp))
                        .clip(CircleShape),
                    bitmap = contactInfo.photo.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen._80dp))
                        .clip(CircleShape),
                    painter = painterResource(id = R.drawable.no_image_contact),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen._7dp)))
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._7dp))) {
                Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._5dp))) {
                    Text(
                        text = contactInfo.firstName,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = _20sp, fontWeight = FontWeight.W500)
                    )
                    contactInfo.lastName?.let { lastName ->
                        Text(
                            text = lastName,
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = _20sp)
                        )
                    }
                }
                Text(
                    text = contactInfo.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = _17sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = R.dimen._13dp),
                    end = dimensionResource(id = R.dimen._10dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = contactInfo.id.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = _15sp
            )
            Text(
                text = contactInfo.timeStamp.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = _18sp,
                    fontWeight = FontWeight.W500,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}