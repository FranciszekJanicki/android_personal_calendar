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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var recurrence by remember { mutableStateOf(Recurrence.NONE) }
    var error by remember { mutableStateOf<String?>(null) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var editingEventId by remember { mutableStateOf<String?>(null) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val expandedEventIds = remember { mutableStateListOf<String>() }
    val dataStoreManager = remember { DataStoreManager(context) }

    LaunchedEffect(Unit) {
        try {
            dataStoreManager.getEvents().collect {
                events = it
            }
        } catch (e: Exception) {
            error = "Failed to load events: ${e.localizedMessage}"
            e.printStackTrace()
        }
    }

    fun clearForm() {
        title = ""
        description = ""
        startDateTime = null
        endDateTime = null
        recurrence = Recurrence.NONE
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

            Spacer(modifier = Modifier.height(8.dp))

            DateTimePickerField(
                label = "Data rozpoczęcia",
                dateTime = startDateTime,
                onDateTimeChange = { startDateTime = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateTimePickerField(
                label = "Data zakończenia",
                dateTime = endDateTime,
                onDateTimeChange = { endDateTime = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            RecurrenceDropdown(
                recurrence = recurrence,
                onRecurrenceSelected = { recurrence = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    if (title.isBlank()) {
                        error = "Nazwa nie może być pusta"
                        return@Button
                    }
                    if (startDateTime == null) {
                        error = "Proszę wybrać datę rozpoczęcia"
                        return@Button
                    }
                    if (endDateTime == null) {
                        error = "Proszę wybrać datę zakończenia"
                        return@Button
                    }
                    if (endDateTime!!.isBefore(startDateTime)) {
                        error = "Data zakończenia nie może być wcześniej niż rozpoczęcia"
                        return@Button
                    }

                    error = null
                    coroutineScope.launch {
                        val newEvent = Event(
                            id = editingEventId ?: UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            startDateTime = startDateTime!!,
                            endDateTime = endDateTime!!,
                            recurrence = recurrence ?: Recurrence.NONE
                        )
                        val newList = if (editingEventId == null) {
                            events + newEvent
                        } else {
                            events.map { if (it.id == editingEventId) newEvent else it }
                        }
                        dataStoreManager.saveEvents(newList)

                        try {
                            NotificationScheduler.scheduleNotification(context, newEvent, beforeStart = true)
                            NotificationScheduler.scheduleNotification(context, newEvent, beforeStart = false)
                        } catch (e: Exception) {
                            e.printStackTrace()
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
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
                                text = "${event.startDateTime.format(formatter)} - ${event.endDateTime.format(formatter)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(text = recurrenceToDisplayName(event.recurrence))

                            if (expandedEventIds.contains(event.id)) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = event.description)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        title = event.title
                                        description = event.description
                                        startDateTime = event.startDateTime
                                        endDateTime = event.endDateTime
                                        recurrence = event.recurrence
                                        editingEventId = event.id
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                                    }

                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            dataStoreManager.saveEvents(events.filter { it.id != event.id })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceDropdown(
    recurrence: Recurrence?,
    onRecurrenceSelected: (Recurrence) -> Unit,
    modifier: Modifier = Modifier
) {
    val safeRecurrence = recurrence ?: Recurrence.NONE
    var expanded by remember { mutableStateOf(false) }
    val recurrenceOptions = Recurrence.values().toList()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = recurrenceToDisplayName(safeRecurrence),
            onValueChange = {},
            label = { Text("Powtarzanie") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            recurrenceOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(recurrenceToDisplayName(option)) },
                    onClick = {
                        onRecurrenceSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    label: String,
    dateTime: LocalDateTime?,
    onDateTimeChange: (LocalDateTime) -> Unit,
) {
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    OutlinedTextField(
        readOnly = true,
        value = dateTime?.format(formatter) ?: "",
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Wybierz datę i czas")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateConfirm = { date ->
                val currentTime = dateTime ?: LocalDateTime.now()
                val combined = LocalDateTime.of(date, currentTime.toLocalTime())
                onDateTimeChange(combined)
                showDatePicker = false
                showTimePicker = true
            }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeConfirm = { time ->
                val currentDate = dateTime?.toLocalDate() ?: LocalDateTime.now().toLocalDate()
                val combined = LocalDateTime.of(currentDate, time)
                onDateTimeChange(combined)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateConfirm: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                DatePicker(state = datePickerState)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Anuluj")
                    }
                    TextButton(onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateConfirm(date)
                        }
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeConfirm: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState()

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)  // prevents edge clipping
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                TimePicker(state = timePickerState)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Anuluj")
                    }
                    TextButton(onClick = {
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute
                        onTimeConfirm(LocalTime.of(hour, minute))
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
