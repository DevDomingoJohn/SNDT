package com.domin.sndt.info.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.domin.sndt.info.ActiveConnectionState
import com.domin.sndt.info.CellDetailsState
import com.domin.sndt.info.ConnectionInfoState
import com.domin.sndt.info.WifiDetailsState

@Composable
fun ActiveConnectionCard(
    state: ActiveConnectionState
) {
    Card(
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Connection Type")
                Text(text = state.connectionType)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "External IP")
                Text(text = state.externalIp)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "External IPv6")
                Text(text = state.externalIpv6)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "HTTP Proxy")
                Text(text = state.httpProxy)
            }
        }
    }
}

@Composable
fun ConnectionInfoCard(
    state: ConnectionInfoState
) {
    Card(
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "IPv4 Address")
                Text(text = state.ipv4Address)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Subnet Mask")
                Text(text = state.subnetMask)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Gateway IPv4")
                Text(text = state.gatewayIpv4)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "DNS Server IPv4")
                Text(text = state.dnsServerIpv4)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "IPv6 Address")
                Text(text = state.ipv6Address)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Gateway IPv6")
                Text(text = state.gatewayIpv6)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "DNS Server IPv6")
                Text(text = state.dnsServerIpv6)
            }
        }
    }
}

@Composable
fun WifiDetailsCard(
    state: WifiDetailsState
) {
    Card(
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Enabled")
                Text(text = state.wifiEnabled)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Connection State")
                Text(text = state.connectionState)
            }
            HorizontalDivider()
            if (state.dhcpLeaseTime != null) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "DHCP Lease Time")
                    Text(text = state.dhcpLeaseTime)
                }
                HorizontalDivider()
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "SSID")
                Text(text = state.ssid)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "BSSID")
                Text(text = state.bssid)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Channel")
                Text(text = state.channel)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Speed (Down / Up)")
                Text(text = state.speed)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Signal Strength")
                Text(text = state.signalStrength)
            }
        }
    }
}

@Composable
fun CellDetailsCard(
    state: CellDetailsState
) {
    Card(
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Data State")
                Text(text = state.dataState)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Data Activity")
                Text(text = state.dataActivity)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Roaming")
                Text(text = state.roaming)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "SIM State")
                Text(text = state.simState)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "SIM Operator Name")
                Text(text = state.simName)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "SIM MCC/MNC")
                Text(text = state.simMccMnc)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Operator Name")
                Text(text = state.operatorName)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "MCC/MNC")
                Text(text = state.mccMnc)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Network Type")
                Text(text = state.networkType)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Phone Type")
                Text(text = state.phoneType)
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Signal Strength")
                Text(text = state.signalStrength)
            }
        }
    }
}