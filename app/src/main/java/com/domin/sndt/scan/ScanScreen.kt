package com.domin.sndt.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ScanScreen(vm: ScanVM = viewModel()) {
    val state by vm.state.collectAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            )
    ) {
        val deviceList = listOf(
            Device("Device 1", "192.168.1.1", "AA:BB:CC:DD:EE:FF"),
            Device("Device 2", "192.168.1.2", "11:22:33:44:55:66"),
            Device("Device 3", "10.0.0.1", "FF:EE:DD:CC:BB:AA")
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(deviceList) { device ->
                DeviceItem(device)
                HorizontalDivider()
            }
        }

        Button(
            onClick = {
                vm.test()
            }
        ) {
            Text("Scan Network")
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ScanScreen()
}