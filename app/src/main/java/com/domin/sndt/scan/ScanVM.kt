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
import javax.inject.Inject

@HiltViewModel
class ScanVM @Inject constructor(
    private val networkInterfaceRepository: NetworkInterfaceRepository
): ViewModel() {

    private val _state = MutableStateFlow<List<Device>>(emptyList())
    val state = _state.asStateFlow()

    fun test() {
        viewModelScope.launch(Dispatchers.IO) {
            val listDevices = networkInterfaceRepository.scanNetwork { device ->
                _state.update { it + device }
            }
            Log.i("Online Devices","Reachable Devices: $listDevices")

        }
    }
}