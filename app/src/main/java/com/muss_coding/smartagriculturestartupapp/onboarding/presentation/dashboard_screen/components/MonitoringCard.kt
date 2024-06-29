package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.components


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun MonitoringCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    info: String,
    background: Color = MaterialTheme.colorScheme.tertiaryContainer
) {
    BasicCard(
        modifier = modifier,
        title = title,
        icon = icon,
        background = background
    ) {
        var fontSize by remember {
            mutableStateOf(32.sp)
        }
        Text(
            text = info,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.hasVisualOverflow) {
                    fontSize *= 0.95f
                }

            }
        )
    }
}