package com.muss_coding.smartagriculturestartupapp.sign_in_out.presentation.signin

import SimpleOutlinedTextField
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muss_coding.smartagriculturestartupapp.R
import com.musscoding.noteit.sign_in.presentation.components.SimpleOutlinedPasswordField

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    signInState: SignInState,
    viewModel: SignInViewModel = hiltViewModel<SignInViewModel>(),
    onSignInClick: () -> Unit,
    background: Color = MaterialTheme.colorScheme.background,
    onSignUpClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = signInState.signInError) {
        signInState.signInError?.let {error ->
            Toast.makeText(
                    context,
                    error,
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    var isRememberMeToggled by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.farming),
                contentDescription = stringResource(R.string.agriculture_image),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentScale = ContentScale.FillBounds
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "WELCOME BACK DEAR!",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        SimpleOutlinedTextField(
            label = "Email",
            leadingIcon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(18.dp))

        SimpleOutlinedPasswordField()

        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Left
            ) {
                Checkbox(
                    checked = isRememberMeToggled,
                    onCheckedChange = {
                        isRememberMeToggled = it
                    }
                )

                //Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Remember me",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "Forgot password?",
                modifier = Modifier
                    .clickable {

                    },
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Or sign in with",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
                    .clickable {
                               onSignInClick()
                    },
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "Google sign in",
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
                    .clickable {  },
                painter = painterResource(id = R.drawable.facebook_logo),
                contentDescription = "Facebook sign in",
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier.fillMaxWidth(.85f),
            onClick = { /*TODO*/ }
        ) {
            Text(text = "Login")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(.85f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account? ", color = MaterialTheme.colorScheme.onBackground)
            Text(
                text = "Sign up now",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onSignUpClick()
                }
            )
        }
    }
}