package com.example.perpetualcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import java.time.LocalDate
import java.util.Base64
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

    NavHost(navController, startDestination = "main") {
        composable("main") {
            HolidayScreen(
                onShowSundays = { year, easter ->
                    val easterEncoded = easter.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    navController.navigate("sundays/$year/$easterEncoded")
                },
                onShowWorkdays = {
                    navController.navigate("workdays")
                }
            )
        }

        composable(
            "sundays/{year}/{easter}",
            arguments = listOf(
                navArgument("year") { type = NavType.IntType },
                navArgument("easter") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val year = backStackEntry.arguments?.getInt("year") ?: -1
            val easterStr = backStackEntry.arguments?.getString("easter")
            val easter = try {
                LocalDate.parse(easterStr)
            } catch (e: Exception) {
                null
            }

            SundaysScreen(year, easter, onBack = { navController.popBackStack() })
        }

        composable("workdays") {
            WorkdaysScreen(onBack = { navController.popBackStack() })
        }
    }
}

