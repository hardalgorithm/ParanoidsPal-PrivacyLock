package com.paranoid.privacylock.services

import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import com.paranoid.privacylock.ui.MainActivity
import com.paranoid.privacylock.util.enableLockdown
import com.paranoid.privacylock.util.isDeviceAdminActive

class ParanoidsTileService:TileService() {

    override fun onClick() {
        super.onClick()

        Runnable {
            if (!isDeviceAdminActive(applicationContext)) {
                startMainActivity()
            }else{
                    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    if (!keyguardManager.isKeyguardLocked)
                enableLockdown(applicationContext)
            }

        }.also { runnable ->
            if (this.isLocked) {
                if (!isDeviceAdminActive(applicationContext)) {
                    startMainActivity()
                }else {
                    /** At this moment device finger locked and user press tileButton
                    for correct working we need pin block device */
                    enableLockdown(applicationContext)
                }
                this.unlockAndRun(runnable)
            } else {
                runnable.run()
            }
        }
    }

    private fun startMainActivity(){
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            startActivityAndCollapse(pendingIntent)
        } else startActivityAndCollapse(intent)
    }

}