package com.example.perpetualcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarApp()
        }
    }
}

@Composable
fun CalendarApp() {
    val navController = rememberNavController()
    val currDate = remember { mutableStateOf<LocalDate>() }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("main") {
                MainScreen(
                    onShowEvents = {
                        navController.navigate("events")
                    },
                    onShowSettings = {
                        navController.navigate("settings")
                    },
                    onShowSundays = {
                        navController.navigate("sundays")
                    },
                    onShowWorkdays = {
                        navController.navigate("workdays")
                    },
                    onShowDateDiff = {
                        navController.navigate("datediff")
                    }
                )
            }

            composable("events") {
                EventsScreen(currDate = currDate.value, onBack = { navController.popBackStack() })
            }

            composable("settings") {
                SettingsScreen(currDate = currDate.value, onBack = { navController.popBackStack() })
            }

            composable("sundays") {
                SundaysScreen(currDate = currDate.value, onBack = { navController.popBackStack() })
            }

            composable("workdays") {
                WorkdaysScreen(onBack = { navController.popBackStack() })
            }

            composable("datediff") {
                DateDiffScreen(onBack = { navController.popBackStack() })
            }

        }
    }
}

