package com.example.perpetualcalendar

import android.content.Context
import androidx.compose.foundation.clickable
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
    var editingEventId by remember { mutableStateOf<String?>(null) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now()
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Track which event descriptions are expanded (by id)
    val expandedEventIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        dataStoreManager.getEvents().collect { events = it }
    }

    fun clearForm() {
        title = ""
        description = ""
        start = ""
        end = ""
        editingEventId = null
        error = null
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
            Text(
                if (editingEventId == null) "Dodaj nowe wydarzenie" else "Edytuj wydarzenie",
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 5
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Button(
                    onClick = {
                        try {
                            val startDate = LocalDate.parse(start, formatter)
                            val endDate = LocalDate.parse(end, formatter)

                            when {
                                title.isBlank() -> error = "Nazwa wydarzenia nie może być pusta"
                                startDate.isBefore(today) || endDate.isBefore(today) ->
                                    error = "Daty nie mogą być wcześniejsze niż dzisiaj"
                                endDate.isBefore(startDate) ->
                                    error = "Data zakończenia nie może być przed datą rozpoczęcia"
                                else -> {
                                    if (editingEventId == null) {
                                        // Add new event
                                        val newEvent = Event(
                                            id = UUID.randomUUID().toString(),
                                            title = title,
                                            startDate = startDate,
                                            endDate = endDate,
                                            description = description
                                        )
                                        events = events + newEvent
                                    } else {
                                        // Edit existing event
                                        events = events.map {
                                            if (it.id == editingEventId) {
                                                it.copy(
                                                    title = title,
                                                    startDate = startDate,
                                                    endDate = endDate,
                                                    description = description
                                                )
                                            } else it
                                        }
                                    }

                                    coroutineScope.launch {
                                        dataStoreManager.saveEvents(events)
                                    }
                                    clearForm()
                                }
                            }
                        } catch (e: Exception) {
                            error = "Niepoprawny format daty"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (editingEventId == null) "Dodaj wydarzenie" else "Zapisz zmiany")
                }

                if (editingEventId != null) {
                    OutlinedButton(
                        onClick = { clearForm() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Anuluj")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Twoje wydarzenia", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(events) { index, event ->
                    val expanded = expandedEventIds.contains(event.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (expanded) {
                                    expandedEventIds.remove(event.id)
                                } else {
                                    expandedEventIds.add(event.id)
                                }
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(event.title, style = MaterialTheme.typography.bodyLarge)
                            Text("Od: ${event.startDate}", style = MaterialTheme.typography.bodyMedium)
                            Text("Do: ${event.endDate}", style = MaterialTheme.typography.bodyMedium)

                            if (expanded && event.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(event.description, style = MaterialTheme.typography.bodySmall)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = {
                                        // Start editing event
                                        editingEventId = event.id
                                        title = event.title
                                        description = event.description
                                        start = event.startDate.toString()
                                        end = event.endDate.toString()
                                        error = null
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                                }

                                IconButton(
                                    onClick = {
                                        events = events.toMutableList().apply { removeAt(index) }
                                        coroutineScope.launch {
                                            dataStoreManager.saveEvents(events)
                                        }
                                        if (editingEventId == event.id) clearForm()
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
