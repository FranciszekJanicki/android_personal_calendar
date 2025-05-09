package com.example.perpetualcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

class Sundays : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val year = intent.getIntExtra("year", -1)
        val easterDate = intent.getSerializableExtra("easter") as? LocalDate

        setContent {
            SundaysScreen(year, easterDate, onBack = { finish() })
        }
    }
}

@Composable
fun SundaysScreen(year: Int, easterDate: LocalDate?, onBack: () -> Unit) {
    val message: String
    val sundays: List<String>

    if (year <= 0 || easterDate == null) {
        message = "Błąd danych wejściowych - " +
                if (easterDate == null) "daty wielkanocy" else "roku"
        sundays = emptyList()
    } else if (year < 2020) {
        message = "Niedziele handlowe w $year roku nie mają sensu"
        sundays = emptyList()
    } else {
        message = "Niedziele handlowe w $year roku"
        sundays = buildSundayList(year, easterDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Handlowe niedziele") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        ) {
            Text(text = message, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(sundays.sorted()) { date ->
                    Text(text = date, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

fun buildSundayList(year: Int, easterDate: LocalDate): List<String> {
    val list = mutableList
