package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.components

import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun ControlCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    background: Color = MaterialTheme.colorScheme.tertiaryContainer,
    isItSelected: Boolean,
    onClick: (Boolean) -> Unit
) {
    BasicCard(
        modifier = modifier,
        title = title,
        icon = icon,
        background = background
    ) {
        Switch(
            checked = isItSelected,
            onCheckedChange = {
                onClick(it)
                //isChecked = false
            }
        )
    }
}