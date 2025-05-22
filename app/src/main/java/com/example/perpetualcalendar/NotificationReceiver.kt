package com.example.perpetualcalendar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Event Reminder"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: ""

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showNotification(context, id, title, message)
    }
}
