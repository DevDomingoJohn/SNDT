package com.domin.sndt.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
import com.domin.sndt.info.components.ConnectionDetailsCard
import com.domin.sndt.info.components.WifiConnectionCard
import com.domin.sndt.info.components.WifiDetailsCard

@Composable
fun InfoScreen(vm: InfoVM = viewModel()) {
    val state by vm.state.collectAsState()

    LaunchedEffect(key1 = true) {
        vm.getWifiInfo()
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
    ) {
        Text(
            text = "CONNECTION DETAILS",
            style = MaterialTheme.typography.titleSmall
        )

        ConnectionDetailsCard(state)

        Spacer(modifier = Modifier.height(24.dp))

        if (state.connectionType == "Wi-Fi") {
            Text(
                text = "WI-FI Connection",
                style = MaterialTheme.typography.titleSmall
            )

            WifiConnectionCard(state)

            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = "WI-FI DETAILS",
            style = MaterialTheme.typography.titleSmall
        )

        WifiDetailsCard(state)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InfoScreenPreview() {
    InfoScreen()
}