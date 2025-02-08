package com.domin.sndt.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sndt.ConnectivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class ScanVM @Inject constructor(
    private val connectivityRepository: ConnectivityRepository
): ViewModel() {

    private val _state = MutableStateFlow<List<String>>(emptyList())
    val state = _state.asStateFlow()

    fun isDeviceOnline(ip: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = InetAddress.getByName(ip)
            if (address.isReachable(1000)) { // Timeout in milliseconds
                _state.update { it + ip }
            } else {
                _state.update { it + "not reachable" }
            }
        }
    }

    fun test() {
        viewModelScope.launch {
            connectivityRepository.getLocalIp()
        }
    }
}