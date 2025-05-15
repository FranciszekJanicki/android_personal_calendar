package com.example.perpetualcalendar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "events_data_store")

class DataStoreManager(private val context: Context) {
    private val eventsKey = stringPreferencesKey("events_list")

    private val gson = GsonBuilder()
        .registerTypeAdapter(java.time.LocalDate::class.java, LocalDateAdapter())
        .create()

    suspend fun saveEvents(events: List<Event>) {
        val eventsJson = gson.toJson(events)
        context.dataStore.edit { preferences ->
            preferences[eventsKey] = eventsJson
        }
    }

    fun getEvents(): Flow<List<Event>> {
        return context.dataStore.data.map { preferences ->
            val eventsJson = preferences[eventsKey] ?: "[]"
            val listType = object : TypeToken<List<Event>>() {}.type
            try {
                gson.fromJson<List<Event>>(eventsJson, listType)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun clearEvents() {
        context.dataStore.edit { it.remove(eventsKey) }
    }
}