package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var defaultYear by remember { mutableStateOf("2024") }
    var showSavedMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = defaultYear,
                onValueChange = { defaultYear = it },
                label = { Text("Domyślny rok") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Future: persist to settings datastore/sharedprefs
                    showSavedMessage = true
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Zapisz")
            }

            if (showSavedMessage) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Ustawienia zapisane!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
