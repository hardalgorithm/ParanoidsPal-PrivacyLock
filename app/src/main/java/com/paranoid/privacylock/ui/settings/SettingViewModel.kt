package com.paranoid.privacylock.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.paranoid.privacylock.storage.ParanoidSettingStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingStorage: ParanoidSettingStorage
): ViewModel() {

    private val _isToggleChecked = MutableLiveData(false)
    val isToggleChecked: LiveData<Boolean> get() = _isToggleChecked

    private val _isToggleVibrateChecked = MutableLiveData(true)
    val isToggleVibrateChecked: LiveData<Boolean> get() = _isToggleVibrateChecked

    private val _seekBarProgress = MutableLiveData(0)
    val seekBarProgress: LiveData<Int> get() = _seekBarProgress


    init {
        _isToggleChecked.value = settingStorage.isActiveShake
        _isToggleVibrateChecked.value = settingStorage.isActiveVibrate
        _seekBarProgress.value = settingStorage.seekBarState
    }

    fun saveState() {
        settingStorage.seekBarState = _seekBarProgress.value ?: 0
    }

    fun saveToggleChecked(isChecked: Boolean) {
        _isToggleChecked.value = isChecked
        settingStorage.isActiveShake = isChecked
    }

    fun saveToggleVibrateChecked(isChecked: Boolean) {
        _isToggleVibrateChecked.value = isChecked
        settingStorage.isActiveVibrate = isChecked
    }

    fun getToggleVibrateChecked(): Boolean{
        return settingStorage.isActiveVibrate
    }

    fun setSeekBarProgress(progress: Int) {
        _seekBarProgress.value = progress
    }

}