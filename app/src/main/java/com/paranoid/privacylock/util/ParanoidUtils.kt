package com.paranoid.privacylock.util

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.paranoid.privacylock.receivers.MyAdminReceiver

fun enableLockdown(context: Context) {
    val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val componentName = ComponentName(context, MyAdminReceiver::class.java)

    if (!devicePolicyManager.isAdminActive(componentName)) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return
    }
    devicePolicyManager.lockNow()
}

fun isDeviceAdminActive(context: Context): Boolean {
    val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val componentName = ComponentName(context, MyAdminReceiver::class.java)
    return devicePolicyManager.isAdminActive(componentName)
}
