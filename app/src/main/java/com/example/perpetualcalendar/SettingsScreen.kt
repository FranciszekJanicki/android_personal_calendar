package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val showDescriptionsFlow = dataStoreManager.showDescriptionsFlow.collectAsState(initial = true)
    val enableNotificationsFlow = dataStoreManager.enableNotificationsFlow.collectAsState(initial = false)

    var showDescriptions by remember { mutableStateOf(showDescriptionsFlow.value) }
    var enableNotifications by remember { mutableStateOf(enableNotificationsFlow.value) }
    var showSavedMessage by remember { mutableStateOf(false) }

    LaunchedEffect(showDescriptionsFlow.value) { showDescriptions = showDescriptionsFlow.value }
    LaunchedEffect(enableNotificationsFlow.value) { enableNotifications = enableNotificationsFlow.value }

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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = showDescriptions,
                    onCheckedChange = { showDescriptions = it }
                )
                Spacer(Modifier.width(8.dp))
                Text("Pokaż opisy wydarzeń")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = enableNotifications,
                    onCheckedChange = { enableNotifications = it }
                )
                Spacer(Modifier.width(8.dp))
                Text("Włącz powiadomienia")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveShowDescriptions(showDescriptions)
                        dataStoreManager.saveEnableNotifications(enableNotifications)
                        showSavedMessage = true
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Zapisz")
            }

            if (showSavedMessage) {
                Text(
                    "Ustawienia zapisane!",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
