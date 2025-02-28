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

    fun getConnectionInfo() {
        viewModelScope.launch {
            val connectionDetails = connectivityManagerRepository.getConnectionDetails()
            if (connectionDetails != null) {
                val activeConnection = connectionDetails.first
                val connectionInfo = connectionDetails.second
                _uiState.update { it.copy(
                    activeConnectionState = ActiveConnectionState(
                        activeConnection.connectionType ?: "N/A",
                        activeConnection.externalIp ?: "N/A",
                        activeConnection.externalIpv6 ?: "N/A",
                        activeConnection.httpProxy ?: "N/A"
                    ),
                    connectionInfoState = ConnectionInfoState(
                        connectionInfo.ipv4Address ?: "N/A",
                        connectionInfo.subnetMask ?: "N/A",
                        connectionInfo.gatewayIpv4 ?: "N/A",
                        connectionInfo.dnsServerIpv4 ?: "N/A",
                        connectionInfo.ipv6Address ?: "N/A",
                        connectionInfo.gatewayIpv6 ?: "N/A",
                        connectionInfo.dnsServerIpv6 ?: "N/A"
                    )
                ) }

                when (activeConnection.connectionType) {
                    "Wi-Fi" -> {
                        val wifiDetails = connectivityManagerRepository.getWifiDetails()
                        if (wifiDetails != null) {
                            val dhcpLeaseTime = if (wifiDetails.dhcpLeaseTime != null)
                                wifiDetails.dhcpLeaseTime.toString() else null
                            _uiState.update { it.copy(
                                wifiDetailsState = WifiDetailsState(
                                    wifiDetails.wifiEnabled.toString(),
                                    wifiDetails.connectionState ?: "N/A",
                                    dhcpLeaseTime,
                                    wifiDetails.ssid ?: "N/A",
                                    wifiDetails.bssid ?: "N/A",
                                    wifiDetails.channel ?: "N/A",
                                    wifiDetails.speed ?: "N/A",
                                    wifiDetails.signalStrength ?: "N/A"
                                )
                            ) }
                        }
                    }
                }
            }
        }
    }
}