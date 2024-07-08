package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.profile_screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.muss_coding.smartagriculturestartupapp.R
import com.muss_coding.smartagriculturestartupapp.sign_in_out.presentation.signin.UserData

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userData: UserData?
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
        )
        // Profile picture
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = userData?.profilePictureUrl,
                contentDescription = stringResource(R.string.profile_picture),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable {
                        Toast
                            .makeText(
                                context,
                                "Clicked me",
                                Toast.LENGTH_LONG
                            )
                            .show()
                    },
                contentScale = ContentScale.Crop
            )
        }
        // Profile information
        Text(
            text = userData?.userName ?: "",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }
}