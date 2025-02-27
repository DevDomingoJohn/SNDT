package com.domin.sndt.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domin.sndt.core.domain.ConnectivityManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoVM @Inject constructor(
    private val connectivityManagerRepository: ConnectivityManagerRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _state = MutableStateFlow(ConnectionDetails())
    val state = _state.asStateFlow()

    fun getConnectionInfo() {
        viewModelScope.launch {
            val connectionType = connectivityManagerRepository.getConnectionType()

            val wifiDetails = connectivityManagerRepository.getConnectionInfo()
            _state.update { wifiDetails }
        }
    }
}