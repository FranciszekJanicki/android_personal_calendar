package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class EventType {
    BIRTHDAY,
    PERSONAL,
    WORK,
    OTHER,
    NONE,
}

data class EventData (
    var beginDate : LocalDate,
    var endDate : LocalDate,
    var isCyclic : Boolean,
    var userNotes : String,
    var useNotif : Boolean) {
}

data class Event (
    var type : EventType = EventType.NONE,
    var data : EventData) {
}

@Composable
fun EventsScreen(currDate : LocalDate, onBack: () -> Unit) {
    val events by remember { mutableStateOf<MutableList<Event>>(mutableListOf()) }
    val lastEvent by remember { mutableStateOf<Event>() }
    val firstEvent by remember { mutableStateOf<Event>() }

    val headerText = "Eventy"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = headerText,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(events.sorted()) { date ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Powrót", style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun sortedEventsByDate(events: MutableList<Event>): MutableList<Event> {
    return events.sortedBy { it.data.begin_date }.toMutableList()
}
