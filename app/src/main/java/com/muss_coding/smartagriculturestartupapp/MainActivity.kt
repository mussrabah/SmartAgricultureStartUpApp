package com.muss_coding.smartagriculturestartupapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.muss_coding.irrigation.IrrigationActivity
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.DashboardEvents
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.DashboardScreen
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.dashboard_screen.DashboardViewModel
import com.muss_coding.smartagriculturestartupapp.onboarding.presentation.profile_screen.ProfileScreen
import com.muss_coding.smartagriculturestartupapp.ui.theme.SmartAgricultureStartUpAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartAgricultureStartUpAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val dashboardViewModel: DashboardViewModel = hiltViewModel()
                    RequestLocationPermissionAndFetchWeather(dashboardViewModel)
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard"
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                state = dashboardViewModel.state.collectAsState().value,
                                onSelectFeature = { index ->
                                    // Handle feature selection
                                    val intent = Intent(Intent.ACTION_MAIN)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    when (index) {
                                        0 -> {
//                                            intent.setPackage("pe.edu.upc.upet") // Package name of the other app
//                                            intent.setClassName("pe.edu.upc.upet", "pe.edu.upc.upet.MainActivity")
                                            val intentSoil = Intent(this@MainActivity, com.muss_coding.soil_type.SoilClassificationActivity::class.java)
                                            startActivity(intentSoil)
                                        }

                                        1 -> {
                                            val intentIrrigation = Intent(this@MainActivity, IrrigationActivity::class.java)
                                            startActivity(intentIrrigation)
                                        }

                                        2 -> {
                                            val intentCrop = Intent(this@MainActivity, com.muss_coding.crop_recommendation.Dashboard::class.java)
                                            startActivity(intentCrop)
                                        }
                                    }
                                    //startActivity(this@MainActivity, intent, null)
                                },
                                navigateToProfileScreen = {
                                    navController.navigate("profile")
                                },
                                enterNewScreen = {
                                    //app.ij.errigation
                                    val intent = Intent(Intent.ACTION_MAIN)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.setPackage("app.ij.errigation") // Package name of the other app
                                    intent.setClassName("app.ij.errigation", "app.ij.errigation.MainActivity")
                                    startActivity(this@MainActivity, intent, null)
                                },
                                updateControls = { index, isChecked ->
                                    dashboardViewModel.onEvent(DashboardEvents.OnUpdateControlToggle(index, isChecked))
                                }
                            )
                        }

                        composable("profile") {
                            ProfileScreen()
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun RequestLocationPermissionAndFetchWeather(
        dashboardViewModel: DashboardViewModel
    ) {
        val locationPermissionState =
            rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
        val context = LocalContext.current

        val locationManager =
            LocalContext.current.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        LaunchedEffect(locationPermissionState.status, isLocationEnabled) {
            if (!locationPermissionState.status.isGranted) {
                locationPermissionState.launchPermissionRequest()
            } else if (isLocationEnabled) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            dashboardViewModel.updateLocation(location)
                        } else {
                            val locationRequest = LocationRequest.create().apply {
                                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                                interval = 1000L // Update interval in milliseconds
                                fastestInterval = 500L // Fastest update interval in milliseconds
                            }
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                object : LocationCallback() {
                                    override fun onLocationResult(locationResult: LocationResult) {
                                        super.onLocationResult(locationResult)
                                        locationResult.lastLocation.let {
                                            dashboardViewModel.updateLocation(it)
                                            fusedLocationClient.removeLocationUpdates(this)
                                        }
                                    }
                                },
                                Looper.getMainLooper()
                            )
                        }
                    }
                } catch (e: SecurityException) {
                    // Handle the security exception
                }
            } else {
                // Guide user to enable location services
                Toast.makeText(this@MainActivity, "enable location", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




