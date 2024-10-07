package com.paranoid.privacylock.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ParanoidSettingStorage @Inject constructor(
        @ApplicationContext private val context: Context
) {
    companion object {
       const val KEY_ENABLE_MINUTES = "kem"
       const val KEY_IS_ACTIVE_SHAKE = "ias"
       const val KEY_IS_ACTIVE_VIBRATE = "iav"
       const val KEY_SEEK_BAR_STATE = "sbs"
    }

    private var mPreferences: SharedPreferences = EncryptedSharedPreferences(
        context = context,
        fileName = "${context.packageName}.sec",
        masterKey = MasterKey(context)
    )

    var enabledMinutes: Int
        get() = mPreferences.getInt(KEY_ENABLE_MINUTES, 0)
        set(value) = with(mPreferences.edit()) {
            putInt(KEY_ENABLE_MINUTES, value)
        }.apply()

    var isActiveShake: Boolean
        get() = mPreferences.getBoolean(KEY_IS_ACTIVE_SHAKE, false)
        set(value) = with(mPreferences.edit()){
            putBoolean(KEY_IS_ACTIVE_SHAKE, value)
        }.apply()

    var isActiveVibrate: Boolean
        get() = mPreferences.getBoolean(KEY_IS_ACTIVE_VIBRATE, true)
        set(value) = with(mPreferences.edit()){
            putBoolean(KEY_IS_ACTIVE_VIBRATE, value)
        }.apply()


    var seekBarState: Int
        get() = mPreferences.getInt(KEY_SEEK_BAR_STATE, 1)
        set(value) = with(mPreferences.edit()) {
            putInt(KEY_SEEK_BAR_STATE, value)
        }.apply()
}