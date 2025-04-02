package com.domin.sndt.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sndt.core.domain.repo.NetworkInterfaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    fun scanNetwork() {
        viewModelScope.launch {
            networkInterfaceRepository.scanNetwork(
                deviceReached = { device ->
                    _state.update { it + device }
                },
                isScanning = { isScanning ->
                    _isScanning.update { isScanning }
                }
            )
        }
    }
}