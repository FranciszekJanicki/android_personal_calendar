package com.example.perpetualcalendar

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.perpetualcalendar.ui.theme.PerpetualCalendarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perpetual Calendar") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("events") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Wyświetl Wydarzenia")
                }

                Button(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Ustawienia")
                }

                Button(
                    onClick = { navController.navigate("holidays") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Święta")
                }

                Button(
                    onClick = { navController.navigate("datediff") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Różnica Dni")
                }

                Button(
                    onClick = { navController.navigate("shopping") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Lista Zakupów")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PerpetualCalendarTheme {
        MainScreen(navController = rememberNavController())
    }
}
