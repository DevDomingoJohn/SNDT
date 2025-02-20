package com.domin.sndt.scan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DeviceItem(device: Device) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (device.label != null) {
            Text(
                text = device.label,
                fontWeight = FontWeight.Bold
            )
        }
        Text(text = "IP: ${device.ipAddress}")
        Text(text = "MAC: ${device.macAddress}")
        if (device.vendor != null) {
            Text(text = device.vendor)
        }
    }
}