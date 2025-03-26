package com.domin.sndt.info

data class UIState(
    val activeConnectionState: ActiveConnectionState = ActiveConnectionState(),
    val wifiInfoState: WifiInfoState = WifiInfoState(),
    val wifiDetailsState: WifiDetailsState = WifiDetailsState(),
    val cellDetailsState: CellDetailsState = CellDetailsState(),
    val activeConnectionList: List<String> = emptyList<String>()
)
