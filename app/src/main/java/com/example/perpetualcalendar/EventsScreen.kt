package com.example.perpetualcalendar

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var editEvent by remember { mutableStateOf<Event?>(null) } // currently editing event

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now()
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        dataStoreManager.getEvents().collect { events = it }
    }

    // Helper to reset form inputs
    fun resetForm() {
        title = ""
        description = ""
        start = ""
        end = ""
        error = null
        editEvent = null
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
            val isEditing = editEvent != null
            Text(
                if (isEditing) "Edytuj wydarzenie" else "Dodaj nowe wydarzenie",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nazwa wydarzenia") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis wydarzenia (opcjonalnie)") },
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

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    try {
                        val startDate = LocalDate.parse(start, formatter)
                        val endDate = LocalDate.parse(end, formatter)

                        when {
                            startDate.isBefore(today) || endDate.isBefore(today) ->
                                error = "Daty nie mogą być wcześniejsze niż dzisiaj"
                            endDate.isBefore(startDate) ->
                                error = "Data zakończenia nie może być przed datą rozpoczęcia"
                            title.isBlank() ->
                                error = "Nazwa wydarzenia nie może być pusta"
                            else -> {
                                if (isEditing) {
                                    // Update existing event
                                    val updatedEvent = editEvent!!.copy(
                                        title = title,
                                        description = description,
                                        startDate = startDate,
                                        endDate = endDate
                                    )
                                    events = events.map { if (it.id == updatedEvent.id) updatedEvent else it }
                                } else {
                                    // Add new event
                                    val newEvent = Event(
                                        id = UUID.randomUUID().toString(),
                                        title = title,
                                        startDate = startDate,
                                        endDate = endDate,
                                        description = description
                                    )
                                    events = events + newEvent
                                }
                                coroutineScope.launch { dataStoreManager.saveEvents(events) }
                                resetForm()
                            }
                        }
                    } catch (e: Exception) {
                        error = "Niepoprawny format daty"
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(if (isEditing) "Zapisz zmiany" else "Dodaj wydarzenie")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Twoje wydarzenia", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
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
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(event.title, style = MaterialTheme.typography.bodyLarge)
                                Text("Od: ${event.startDate}", style = MaterialTheme.typography.bodyMedium)
                                Text("Do: ${event.endDate}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        editEvent = event
                                        title = event.title
                                        description = event.description
                                        start = event.startDate.format(formatter)
                                        end = event.endDate.format(formatter)
                                        error = null
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                                }
                                IconButton(
                                    onClick = {
                                        events = events.toMutableList().apply { removeAt(index) }
                                        coroutineScope.launch { dataStoreManager.saveEvents(events) }
                                        if (editEvent?.id == event.id) resetForm()
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
}
