package com.paranoid.privacylock.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.paranoid.privacylock.R
import com.paranoid.privacylock.util.enableLockdown


class LockService: Service() {

    private val CHANNEL_ID = "paranoid_channel"
    private val NOTIFICATION_ID = 1
    private var minutesDelayed: Int = 0
    private val lockReceiver: LockReceiver by lazy { LockReceiver() }
    private val handler = android.os.Handler()
    private var startTime: Long = 0L
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification(),
                    FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
                )
            }
        } else {
            startForeground(NOTIFICATION_ID, buildNotification())
        }
    }

    /**
     * startForeground a notification must be in both onCreate and onStartCommand, because if
     * your service is already created and somehow your activity is trying to start it again,
     * onCreate won't be called.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        minutesDelayed = intent?.getIntExtra("minutesDelay", 0) ?: 0
        startLockReceiver()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startLockReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(lockReceiver, intentFilter)
    }

    private fun stopLockReceiver() {
        unregisterReceiver(lockReceiver)
    }

    private fun buildNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_complete)
            .setContentTitle("Paranoid Timer Service")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        return builder.build()
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Paranoid Channel", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLockReceiver()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private inner class LockReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {

                when (intent.action) {

                    Intent.ACTION_USER_PRESENT -> {}

                    Intent.ACTION_SCREEN_ON -> {
                        isRunning = false
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        startTime = System.currentTimeMillis()
                        isRunning = true
                        startCheckLoop(context, minutesDelayed)
                    }
                }
            }
        }
    }

    private fun startCheckLoop(context: Context, minutesDelay: Int) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isRunning) {
                    val currentTime = System.currentTimeMillis()
                    val elapsedTime = currentTime - startTime

                    if (elapsedTime >= minutesDelay * 60 * 1000L && isScreenOff(context)) {
                        enableLockdown(context)
                        isRunning = false
                    } else {
                        handler.postDelayed(this, 1000L)
                    }
                }
            }
        }, 1000L)
    }



    private fun isScreenOff(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return !powerManager.isInteractive
    }


}