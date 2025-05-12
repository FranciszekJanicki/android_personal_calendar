package com.example.perpetualcalendar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "events_data_store")

class DataStoreManager(private val context: Context) {

    private val eventsKey = stringPreferencesKey("events_list")

    suspend fun saveEvents(events: List<Event>) {
        val eventsJson = Gson().toJson(events)
        context.dataStore.edit { preferences ->
            preferences[eventsKey] = eventsJson
        }
    }

    fun getEvents(): Flow<List<Event>> {
        return context.dataStore.data
            .map { preferences ->
                val eventsJson = preferences[eventsKey] ?: "[]"
                val listType = object : TypeToken<List<Event>>() {}.type
                Gson().fromJson<List<Event>>(eventsJson, listType)
            }
    }
}
