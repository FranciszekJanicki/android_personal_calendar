package com.example.perpetualcalendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    val shoppingList = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista zakupów") },
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
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Dodaj produkt") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (input.isNotBlank()) {
                            shoppingList.add(input.trim())
                            input = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dodaj")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                itemsIndexed(shoppingList) { index, item ->
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
                            Text(item)
                            IconButton(onClick = { shoppingList.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń")
                            }
                        }
                    }
                }
            }
        }
    }
}
