package com.domin.sndt.scan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sndt.core.domain.NetworkInterfaceRepository
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
    private val networkInterfaceRepository: NetworkInterfaceRepository
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
            val ip = networkInterfaceRepository.getLocalIp()!!
            val subnet = networkInterfaceRepository.getSubnet()!!

            val ipv4Regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$"
            if (ip.contains(Regex(ipv4Regex))) {
                Log.i("SCANVM","$ip is IPv4")
                Log.i("SCANVM","$ip subnet is $subnet")
            }
        }
    }
}