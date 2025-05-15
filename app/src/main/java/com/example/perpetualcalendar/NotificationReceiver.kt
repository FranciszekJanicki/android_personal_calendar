package com.example.perpetualcalendar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.createNotificationChannel(context)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Wydarzenie"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Masz wydarzenie"
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)

        NotificationHelper.showNotification(context, notificationId, title, message)
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
