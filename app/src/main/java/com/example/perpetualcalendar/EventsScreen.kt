package com.example.perpetualcalendar

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val expandedEventIds = remember { mutableStateListOf<String>() }

    var notificationsEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        dataStoreManager.getEvents().collect { events = it }
    }

    LaunchedEffect(Unit) {
        dataStoreManager.enableNotificationsFlow.collect {
            notificationsEnabled = it
        }
    }

    fun clearForm() {
        title = ""
        description = ""
        start = ""
        end = ""
        editingEventId = null
        error = null
    }

    fun scheduleAllNotifications(context: Context, events: List<Event>) {
        events.forEach { event ->
            scheduleNotification(context, event, atStart = true)
            scheduleNotification(context, event, atStart = false)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    // Validate inputs
                    val startDateTime = try {
                        LocalDateTime.parse(start, formatter)
                    } catch (e: Exception) {
                        error = "Niepoprawna data rozpoczęcia"
                        return@Button
                    }
                    val endDateTime = try {
                        LocalDateTime.parse(end, formatter)
                    } catch (e: Exception) {
                        error = "Niepoprawna data zakończenia"
                        return@Button
                    }
                    if (title.isBlank()) {
                        error = "Nazwa nie może być pusta"
                        return@Button
                    }
                    if (endDateTime.isBefore(startDateTime)) {
                        error = "Data zakończenia nie może być wcześniej niż rozpoczęcia"
                        return@Button
                    }

                    error = null
                    coroutineScope.launch {
                        val newEvent = Event(
                            id = editingEventId ?: UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            startDateTime = startDateTime,
                            endDateTime = endDateTime
                        )
                        val newList = if (editingEventId == null) {
                            events + newEvent
                        } else {
                            events.map { if (it.id == editingEventId) newEvent else it }
                        }
                        dataStoreManager.saveEvents(newList)

                        if (notificationsEnabled) {
                            scheduleNotification(context, newEvent, true)
                            scheduleNotification(context, newEvent, false)
                        }

                        clearForm()
                    }
                }) {
                    Text(if (editingEventId == null) "Dodaj" else "Zapisz")
                }

                Button(onClick = { clearForm() }) {
                    Text("Anuluj")
                }
            }

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Powiadomienia")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                        coroutineScope.launch {
                            dataStoreManager.saveEnableNotifications(it)
                            if (!it) {
                                // Cancel all notifications
                                events.forEach { event ->
                                    cancelNotification(context, event, true)
                                    cancelNotification(context, event, false)
                                }
                            } else {
                                // Schedule all notifications
                                scheduleAllNotifications(context, events)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                itemsIndexed(events) { _, event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = {
                            if (expandedEventIds.contains(event.id)) {
                                expandedEventIds.remove(event.id)
                            } else {
                                expandedEventIds.add(event.id)
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${event.startDateTime} - ${event.endDateTime}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (expandedEventIds.contains(event.id)) {
                                Text(text = event.description)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        title = event.title
                                        description = event.description
                                        start = event.startDateTime.format(formatter)
                                        end = event.endDateTime.format(formatter)
                                        editingEventId = event.id
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                                    }

                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            dataStoreManager.saveEvents(events.filter { it.id != event.id })
                                            if (notificationsEnabled) {
                                                cancelNotification(context, event, true)
                                                cancelNotification(context, event, false)
                                            }
                                        }
                                    }) {
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
}
