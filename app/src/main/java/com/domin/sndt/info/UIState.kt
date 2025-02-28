package com.domin.sndt.info

data class UIState(
    val activeConnectionState: ActiveConnectionState = ActiveConnectionState(),
    val connectionInfoState: ConnectionInfoState = ConnectionInfoState(),
    val wifiDetailsState: WifiDetailsState = WifiDetailsState()
)
