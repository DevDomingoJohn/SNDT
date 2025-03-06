package com.domin.sndt.info

import android.telephony.TelephonyManager
import android.util.Log
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
                        val dhcpLeaseTime = wifiDetails.dhcpLeaseTime?.toString()
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
                    "Cell" -> {
                        val cellDetails = connectivityManagerRepository.getCellDetails()
                        val dataState = if (cellDetails.dataState == TelephonyManager.DATA_CONNECTED) "Connected"
                        else "Disconnected"
                        val dataActivity = getDataActivity(cellDetails.dataActivity)
                        val roaming = if (cellDetails.roaming) "Yes" else "No"
                        val simState = getSimState(cellDetails.simState)
                        val networkType = getNetworkTypeName(cellDetails.networkType)
                        val phoneType = getPhoneType(cellDetails.phoneType)

                        _uiState.update { it.copy(
                            cellDetailsState = CellDetailsState(
                                dataState = dataState,
                                dataActivity = dataActivity,
                                roaming = roaming,
                                simState = simState,
                                simName = cellDetails.simName ?: "N/A",
                                simMccMnc = cellDetails.simMccMnc ?: "N/A",
                                operatorName = cellDetails.operatorName ?: "N/A",
                                networkType = networkType,
                                phoneType = phoneType
                            )
                        ) }

                        connectivityManagerRepository.getCellSignalStrength { dbm ->
                            if (dbm != null) {
                                _uiState.update { it.copy(
                                    cellDetailsState = it.cellDetailsState.copy(signalStrength = dbm.toString())
                                ) }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getDataActivity(dataActivity: Int?): String {
        return when (dataActivity) {
            TelephonyManager.DATA_ACTIVITY_IN -> "In"
            TelephonyManager.DATA_ACTIVITY_OUT -> "Out"
            TelephonyManager.DATA_ACTIVITY_INOUT -> "InOut"
            TelephonyManager.DATA_ACTIVITY_DORMANT -> "Dormant"
            TelephonyManager.DATA_ACTIVITY_NONE -> "None"
            else -> "None"
        }
    }

    private fun getSimState(simState: Int?): String {
        return when (simState) {
            TelephonyManager.SIM_STATE_ABSENT -> "Absent"
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> "Pin Required"
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> "Puk Required"
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "Network Locked"
            TelephonyManager.SIM_STATE_READY -> "Ready"
            TelephonyManager.SIM_STATE_NOT_READY -> "Not Ready"
            TelephonyManager.SIM_STATE_PERM_DISABLED -> "Permanently Disabled"
            TelephonyManager.SIM_STATE_CARD_IO_ERROR -> "Card IO Error"
            TelephonyManager.SIM_STATE_CARD_RESTRICTED -> "Card Restricted"
            TelephonyManager.SIM_STATE_UNKNOWN -> "Unknown"
            else -> "Unknown"
        }
    }

    private fun getNetworkTypeName(type: Int?): String {
        return when (type) {
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Unknown"
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
            TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO_0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO_A"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO_B"
            TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_EHRPD -> "EHRPD"
            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPAP"
            TelephonyManager.NETWORK_TYPE_NR -> "NR"
            else -> "Unknown"
        }
    }

    private fun getPhoneType(type: Int?): String {
        return when (type) {
            TelephonyManager.PHONE_TYPE_NONE -> "None"
            TelephonyManager.PHONE_TYPE_GSM -> "GSM"
            TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
            TelephonyManager.PHONE_TYPE_SIP -> "SIP"
            else -> "Unknown"
        }
    }
}