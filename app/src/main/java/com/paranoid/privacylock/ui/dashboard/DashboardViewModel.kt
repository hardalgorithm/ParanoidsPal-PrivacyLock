package com.paranoid.privacylock.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.paranoid.privacylock.storage.ParanoidSettingStorage
import com.paranoid.privacylock.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val settingStorage: ParanoidSettingStorage
) : ViewModel() {
    private val _selectedButtonId = MutableLiveData<Int>()
    val selectedButtonId: LiveData<Int> get() = _selectedButtonId

    private val _isPlayingActionButton = MutableLiveData(true)
    val isPlayingActionButton: LiveData<Boolean> get() = _isPlayingActionButton

    init {
        val savedMinutes = settingStorage.enabledMinutes
        val buttonId = mapMinutesToButtonId(savedMinutes)
        _selectedButtonId.value = buttonId
        if (savedMinutes > 0){
            _isPlayingActionButton.value = false
        }
    }

    fun selectButton(buttonId: Int) {
        _selectedButtonId.value = buttonId
    }

    fun togglePlayPauseActionButton() {
        _isPlayingActionButton.value = _isPlayingActionButton.value?.not()
    }

    fun saveCurrentMinuteDelay(minutes: Int){
        settingStorage.enabledMinutes = minutes
    }

    fun getCurrentMinuteDelay():Int{
        return settingStorage.enabledMinutes
    }

    fun getToggleVibrateChecked(): Boolean{
        return settingStorage.isActiveVibrate
    }


    private fun mapMinutesToButtonId(minutes: Int): Int {
        return when (minutes) {
            1 -> R.id.rbOneMinute
            2 -> R.id.rbThreeMinute
            3 -> R.id.rbFiveMinute
            5 -> R.id.rbFifteenMinutes
            10 -> R.id.rbThirtyMinute
            15 -> R.id.rbSixtyMinute
            else -> 0
        }
    }
}