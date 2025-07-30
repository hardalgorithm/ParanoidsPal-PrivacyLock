package com.paranoid.privacylock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.paranoid.privacylock.services.LockService
import com.paranoid.privacylock.services.ShakeService
import com.paranoid.privacylock.storage.ParanoidSettingStorage
import com.paranoid.privacylock.util.isDeviceAdminActive

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_USER_UNLOCKED) {
            if (isDeviceAdminActive(context)) {
                val settingStorage = ParanoidSettingStorage(context)
                if (settingStorage.enabledMinutes>0){
                    val lockServiceIntent = Intent(context, LockService::class.java)
                    lockServiceIntent.putExtras(Bundle().apply {
                        putInt("minutesDelay", settingStorage.enabledMinutes)
                    })
                    ContextCompat.startForegroundService(context, lockServiceIntent)
                }
                if (settingStorage.isActiveShake){
                    val shakeServiceIntent = Intent(context, ShakeService::class.java)
                    ContextCompat.startForegroundService(context, shakeServiceIntent)
                }
            }
        }
    }
}