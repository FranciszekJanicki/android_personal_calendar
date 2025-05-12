package com.example.perpetualcalendar

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val context = LocalContext.current  // Access context here

    // Get the DataStoreManager instance
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Load events from DataStore
    LaunchedEffect(Unit) {
        dataStoreManager.getEvents().collect { eventList ->
            events = eventList
        }
    }

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

            // Event title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nazwa wydarzenia") },
                modifier = Modifier.fillMaxWidth()
            )

            // Start date input
            OutlinedTextField(
                value = start,
                onValueChange = { start = it },
                label = { Text("Data rozpoczęcia (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            // End date input
            OutlinedTextField(
                value = end,
                onValueChange = { end = it },
                label = { Text("Data zakończenia (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Show error message if there is one
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // Add event button
            Button(
                onClick = {
                    try {
                        val startDate = LocalDate.parse(start, formatter)
                        val endDate = LocalDate.parse(end, formatter)
                        val newEvent = Event(title, startDate, endDate)

                        // Add event to local state
                        events = events + newEvent

                        // Save events to DataStore
                        coroutineScope.launch {
                            dataStoreManager.saveEvents(events)
                        }

                        // Clear input fields
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

            // Display events
            Text("Twoje wydarzenia", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                itemsIndexed(events) { index, event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(event.title, style = MaterialTheme.typography.bodyLarge)
                                Text("Od: ${event.startDate}", style = MaterialTheme.typography.bodyMedium)
                                Text("Do: ${event.endDate}", style = MaterialTheme.typography.bodyMedium)
                            }

                            // Delete event button
                            IconButton(
                                onClick = {
                                    events = events.toMutableList().apply { removeAt(index) }
                                    coroutineScope.launch {
                                        dataStoreManager.saveEvents(events)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń")
                            }
                        }
                    }
                }
            }
        }
    }
}
