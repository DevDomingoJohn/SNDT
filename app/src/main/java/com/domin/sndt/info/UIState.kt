package com.domin.sndt.info

data class UIState(
    val activeConnectionState: ActiveConnectionState = ActiveConnectionState(),
    val connectionInfoState: ConnectionInfoState = ConnectionInfoState(),
    val wifiInfoState: WifiInfoState = WifiInfoState(),
    val cellInfoState: CellInfoState = CellInfoState(),
    val wifiDetailsState: WifiDetailsState = WifiDetailsState(),
    val cellDetailsState: CellDetailsState = CellDetailsState()
)
