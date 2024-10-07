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
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.paranoid.privacylock.util.enableLockdown
import com.squareup.seismic.ShakeDetector

class ShakeService: Service(), ShakeDetector.Listener {
    private val CHANNEL_ID = "paranoid_shake"
    private val NOTIFICATION_ID = 2

    private val seekBarReceiver: SeekBarReceiver by lazy { SeekBarReceiver() }

    private val shakeDetector: ShakeDetector = ShakeDetector(this)
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

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        shakeDetector.start(sensorManager, SensorManager.SENSOR_DELAY_NORMAL)

        val filter = IntentFilter("com.paranoid.privacylock.PRIVACYLOCK_SEEKBAR_CHANGE")
        registerReceiver(seekBarReceiver, filter, RECEIVER_EXPORTED)
    }

    /**
     * startForeground a notification must be in both onCreate and onStartCommand, because if
     * your service is already created and somehow your activity is trying to start it again,
     * onCreate won't be called.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Paranoid Shake Service")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        return builder.build()
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Paranoid Channel", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun hearShake() {
            enableLockdown(this@ShakeService)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        unregisterReceiver(seekBarReceiver)
    }

    private inner class SeekBarReceiver: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val progress = p1?.getIntExtra( "progress", 1)
            when(progress){
                0 -> shakeDetector.setSensitivity(13)
                1 -> shakeDetector.setSensitivity(15)
                2 -> shakeDetector.setSensitivity(20)
            }
        }

    }
}