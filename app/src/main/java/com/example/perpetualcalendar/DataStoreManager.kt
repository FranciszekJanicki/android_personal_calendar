package com.example.perpetualcalendar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "events_data_store")

object PreferenceKeys {
    val EVENTS_LIST = stringPreferencesKey("events_list")
    val SHOW_DESCRIPTIONS = booleanPreferencesKey("show_descriptions")
    val ENABLE_NOTIFICATIONS = booleanPreferencesKey("enable_notifications")
}

class DataStoreManager(private val context: Context) {
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    suspend fun saveEvents(events: List<Event>) {
        val eventsJson = gson.toJson(events)
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.EVENTS_LIST] = eventsJson
        }
    }

    fun getEvents(): Flow<List<Event>> {
        return context.dataStore.data.map { preferences ->
            val eventsJson = preferences[PreferenceKeys.EVENTS_LIST] ?: "[]"
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
        context.dataStore.edit { it.remove(PreferenceKeys.EVENTS_LIST) }
    }

    suspend fun saveShowDescriptions(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SHOW_DESCRIPTIONS] = show
        }
    }

    val showDescriptionsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.SHOW_DESCRIPTIONS] ?: true }

    suspend fun saveEnableNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ENABLE_NOTIFICATIONS] = enabled
        }
    }

    val enableNotificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.ENABLE_NOTIFICATIONS] ?: false }
}
