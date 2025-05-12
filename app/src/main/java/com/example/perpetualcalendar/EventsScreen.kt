package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val events = remember { mutableStateListOf<Event>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalendarz osobisty") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            Text("Dodaj nowe wydarzenie", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nazwa wydarzenia") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = start,
                onValueChange = { start = it },
                label = { Text("Data rozpoczęcia (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = end,
                onValueChange = { end = it },
                label = { Text("Data zakończenia (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    try {
                        val startDate = LocalDate.parse(start, formatter)
                        val endDate = LocalDate.parse(end, formatter)
                        events.add(Event(title, startDate, endDate))
                        title = ""
                        start = ""
                        end = ""
                        error = null
                    } catch (e: Exception) {
                        error = "Niepoprawny format daty"
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Dodaj wydarzenie")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Twoje wydarzenia", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(events) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(event.title, style = MaterialTheme.typography.bodyLarge)
                            Text("Od: ${event.startDate}", style = MaterialTheme.typography.bodyMedium)
                            Text("Do: ${event.endDate}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
