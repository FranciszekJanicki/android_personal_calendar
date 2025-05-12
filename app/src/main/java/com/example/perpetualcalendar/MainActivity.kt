package com.example.perpetualcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.perpetualcalendar.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PerpetualCalendarApp()
        }
    }
}

@Composable
fun PerpetualCalendarApp() {
    val navController = rememberNavController()

    // Set up the navigation
    NavHost(navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("events") { EventsScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("datediff") { DateDiffScreen(navController) }
        composable("holidays") { HolidaysScreen(navController) }
        composable("shopping") { ShoppingListScreen(navController) }
    }
}
