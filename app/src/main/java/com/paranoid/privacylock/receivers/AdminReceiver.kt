package com.paranoid.privacylock.receivers

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle

class MyAdminReceiver: DeviceAdminReceiver() {
    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {}
}