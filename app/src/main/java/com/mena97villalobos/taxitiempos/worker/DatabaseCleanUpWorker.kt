package com.mena97villalobos.taxitiempos.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mena97villalobos.taxitiempos.R
import com.mena97villalobos.taxitiempos.database.AppDatabase
import com.mena97villalobos.taxitiempos.database.model.AvailableNumbers

class DatabaseCleanUpWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "DATA_BASE_CLEANUP_NOTIFICATION"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Database Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun doWork(): Result {
        val databaseDao = AppDatabase.getInstance(context).databaseDao
        if (databaseDao.getAllAvailableNumbers().size != 200) {
            databaseDao.deleteAllAvailableNumbers()
            (0..99).forEach {
                databaseDao.insertAvailableNumber(
                    AvailableNumbers(
                        number = it,
                        availableQuantity = 10_000,
                        isDiurna = true
                    )
                )
                databaseDao.insertAvailableNumber(
                    AvailableNumbers(
                        number = it,
                        availableQuantity = 10_000,
                        isDiurna = false
                    )
                )
            }
        }
        databaseDao.resetAvailableNumbers()
        showNotification()
        Log.e(" WORKER", " WORKER EXECUTED")
        return Result.success()
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("Taxi Tiempos")
            .setContentText("Listos para vender tiempos")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        NotificationManagerCompat.from(context).notify(0, builder.build())
    }

}