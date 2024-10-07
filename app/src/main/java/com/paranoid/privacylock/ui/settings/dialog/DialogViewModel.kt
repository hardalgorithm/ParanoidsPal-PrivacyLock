package com.paranoid.privacylock.ui.settings.dialog

import androidx.lifecycle.ViewModel
import com.paranoid.privacylock.storage.ParanoidSettingStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DialogViewModel @Inject constructor(
    private val settingStorage: ParanoidSettingStorage
): ViewModel() {

    fun getToggleVibrateChecked(): Boolean{
        return settingStorage.isActiveVibrate
    }
}