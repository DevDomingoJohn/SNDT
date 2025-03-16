package com.domin.sndt.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domin.sndt.info.components.ActiveConnectionCard
import com.domin.sndt.info.components.CellConnectionInfoCard
import com.domin.sndt.info.components.CellDetailsCard
import com.domin.sndt.info.components.WifiConnectionInfoCard
import com.domin.sndt.info.components.WifiDetailsCard

@Composable
fun InfoScreen(vm: InfoVM = viewModel()) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        vm.getConnectionInfo()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .windowInsetsPadding(
                WindowInsets(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "CONNECTION DETAILS",
            style = MaterialTheme.typography.titleSmall
        )

        ActiveConnectionCard(state.activeConnectionState)

        Spacer(modifier = Modifier.height(24.dp))

        if ("Wi-Fi" in state.activeConnectionList) {
            Text(
                text = "WI-FI CONNECTION",
                style = MaterialTheme.typography.titleSmall
            )

            WifiConnectionInfoCard(state.wifiInfoState)

            Spacer(modifier = Modifier.height(24.dp))
        }

        if ("Cell" in state.activeConnectionList) {
            Text(
                text = "CELL CONNECTION",
                style = MaterialTheme.typography.titleSmall
            )

            CellConnectionInfoCard(state.cellInfoState)

            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = "WI-FI DETAILS",
            style = MaterialTheme.typography.titleSmall
        )

        WifiDetailsCard(state.wifiDetailsState)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CELL DETAILS",
            style = MaterialTheme.typography.titleSmall
        )

        CellDetailsCard(state.cellDetailsState)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InfoScreenPreview() {
    InfoScreen()
}