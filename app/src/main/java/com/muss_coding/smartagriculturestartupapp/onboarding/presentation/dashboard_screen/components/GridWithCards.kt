package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.DashboardState
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.utils.monitoringParameters

@Composable
fun GridWithCards(
    modifier: Modifier = Modifier,
    sizePerRow: Int,
    cards: List<@Composable () -> Unit>,
    state: DashboardState
) {
    val numberOfRows = cards.size / sizePerRow
    val remainingItems = cards.size % sizePerRow

    for (row in 0 until numberOfRows) {
        println("row is: $row")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (column in 0 until sizePerRow) {
                val index = row * sizePerRow + column
                cards[index]()
            }
        }
    }

    if (remainingItems > 0) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (index in cards.size - remainingItems until cards.size) {
                cards[index]()
            }
        }
    }
}