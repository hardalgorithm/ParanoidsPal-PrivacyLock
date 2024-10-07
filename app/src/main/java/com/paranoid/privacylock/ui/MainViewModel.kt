package com.paranoid.privacylock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {
    private val _isFinishedSplashAnimated = MutableStateFlow(false)
    val isFinishedSplashAnimated = _isFinishedSplashAnimated.asStateFlow()

    init {
        viewModelScope.launch {
            delay(400L)
            _isFinishedSplashAnimated.value = true
        }
    }
}