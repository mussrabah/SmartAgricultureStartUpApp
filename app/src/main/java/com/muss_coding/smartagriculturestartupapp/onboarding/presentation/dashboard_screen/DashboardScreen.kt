package com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muss_coding.smartagriculturestartupapp.R
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.components.ControlCard
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.components.GridWithCards
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
            .padding(16.dp)
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.hello))
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.account_button_icon),
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
            title = stringResource(R.string.app_title),
            date = state.lastUpdated.toString(),
        )

        Text(
            text = stringResource(R.string.monitoring),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        val sizePerRow = 2

        GridWithCards(
            sizePerRow = sizePerRow,
            cards = monitoringParameters.mapIndexed()
            { index, item ->
                {
                    MonitoringCard(
                        modifier = Modifier.weight(1f),
                        title = item.title,
                        icon = painterResource(id = item.icon),
                        info = when (index) {
                            0 -> state.waterTemperature
                            1 -> state.waterPh
                            2 -> state.soilMoisture
                            3 -> state.waterLevel
                            else -> ""
                        }.toString()
                    )
                }
            },
            state = state
        )

        Text(
            text = stringResource(R.string.control),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )


        GridWithCards(
            sizePerRow = sizePerRow,
            cards = controlParameters.mapIndexed()
            {index, item ->
                {
                    ControlCard(
                        modifier = Modifier.weight(1f),
                        title = item.title,
                        icon = painterResource(id = item.icon),
                        isItSelected = when(index) {
                            0 -> state.isSprinklingChecked
                            1 -> state.isWateringChecked
                            else -> false
                        }
                    ) {
                        viewModel.onEvent(DashboardEvents.OnUpdateControlToggle(index, it))
                    }
                }
            },
            state = state
        )
        AnimatedVisibility(state.isSprinklingChecked) {
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Sprinkling")
            }
        }
        AnimatedVisibility(state.isWateringChecked) {
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Watering")
            }
        }
    }
}