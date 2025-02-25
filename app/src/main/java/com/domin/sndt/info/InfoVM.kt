package com.domin.sndt.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sndt.core.domain.WifiManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoVM @Inject constructor(
    private val wifiManagerRepository: WifiManagerRepository
): ViewModel() {
    private val _state = MutableStateFlow(ConnectionDetails())
    val state = _state.asStateFlow()

    fun getWifiInfo() {
        viewModelScope.launch {
            val wifiDetails = wifiManagerRepository.getWifiDetails()
            _state.update { wifiDetails }
        }
    }
}