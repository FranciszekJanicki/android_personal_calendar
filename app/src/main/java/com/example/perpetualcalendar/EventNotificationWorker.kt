package com.example.perpetualcalendar

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class EventNotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Wydarzenie"
        val message = inputData.getString("message") ?: "Masz nadchodzące wydarzenie."

        NotificationHelper.sendNotification(applicationContext, 1, title, message)
        return Result.success()
    }
}
