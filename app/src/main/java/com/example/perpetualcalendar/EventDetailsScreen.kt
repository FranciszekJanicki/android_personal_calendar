package com.example.perpetualcalendar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(event: EventDetails, navController: NavController) {
    var description by remember { mutableStateOf(event.description) }
    var note by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły wydarzenia") },
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
            Text(text = event.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Data: ${event.date}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Opis wydarzenia", style = MaterialTheme.typography.bodyMedium)

            BasicTextField(
                value = description,
                onValueChange = { description = it },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { }),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(8.dp)
                    .border(1.dp, Color.Gray)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Notatki", style = MaterialTheme.typography.bodyMedium)

            BasicTextField(
                value = note,
                onValueChange = { note = it },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { }),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(8.dp)
                    .border(1.dp, Color.Gray)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    event.description = description
                    navController.popBackStack()
                }) {
                    Text("Zapisz")
                }
                Button(onClick = {
                    description = event.description
                    note = ""
                }) {
                    Text("Edytuj")
                }
            }
        }
    }
}
