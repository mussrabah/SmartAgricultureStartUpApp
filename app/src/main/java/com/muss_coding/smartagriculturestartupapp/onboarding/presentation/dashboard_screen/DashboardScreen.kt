package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.components.ControlCard
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.components.MainInfoCard
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.components.MonitoringCard
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.utils.controlParameters
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.utils.monitoringParameters

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.background,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Hello,")
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account Button",
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = state.userName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        MainInfoCard(
            title = "Smart Agriculture",
            date = state.lastUpdated.toString(),
        )

        Text(
            text = "Monitoring",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(monitoringParameters) { index, item ->
                MonitoringCard(
                    title = item.title,
                    icon = painterResource(id = item.icon),
                    info = when(index) {
                        0 -> state.waterTemperature
                        1 -> state.waterPh
                        2 -> state.soilMoisture
                        3 -> state.waterLevel
                        else -> ""
                    }.toString()
                )
            }
        }

        Text(
            text = "Control",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        var isItSelected by remember {
            mutableStateOf(false)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(controlParameters) { index, item ->
                isItSelected = item.isItSelected
                ControlCard(
                    title = item.title,
                    icon = painterResource(id = item.icon),
                    isItSelected = when(index) {
                        0 -> state.isSprinklingChecked
                        1 -> state.isWateringChecked
                        else -> false
                    }
                ) {
                    //controlParameters[index] = item.copy(isItSelected = it)
                    //isItSelected = it
                    viewModel.onEvent(DashboardEvents.OnUpdateControlToggle(index, it))
                }
            }
        }
    }
}