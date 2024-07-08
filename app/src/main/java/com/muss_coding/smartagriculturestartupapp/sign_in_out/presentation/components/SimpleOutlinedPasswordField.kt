package com.musscoding.noteit.sign_in.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.muss_coding.smartagriculturestartupapp.R

@Composable
fun SimpleOutlinedPasswordField(
    modifier: Modifier = Modifier,
    label: String = "Password",
    leadingIcon: ImageVector = Icons.Default.Lock
) {
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var isPassVisible by rememberSaveable {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = password,
        onValueChange = {
                        password = it
        },
        modifier = Modifier
            .fillMaxWidth(.85f),
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingIcon.name
            )
        },
        trailingIcon = {
            Icon(
                painter = if (isPassVisible)
                    painterResource(id = R.drawable.baseline_visibility_off_24)
                        else painterResource(id = R.drawable.baseline_visibility_24),
                contentDescription = leadingIcon.name,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        isPassVisible = !isPassVisible
                    }
            )
        },
        visualTransformation = if (isPassVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}