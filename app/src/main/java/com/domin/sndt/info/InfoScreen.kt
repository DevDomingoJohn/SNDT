package com.domin.sndt.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.domin.sndt.core.components.NavigationDrawerState
import com.domin.sndt.info.components.ActiveConnectionCard
import com.domin.sndt.info.components.CellDetailsCard
import com.domin.sndt.info.components.WifiConnectionInfoCard
import com.domin.sndt.info.components.WifiDetailsCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    vm: InfoVM = hiltViewModel(),
    drawerState: NavigationDrawerState
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        vm.getConnectionInfo()
    }

    val coroutine = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Information") },
                navigationIcon = {
                    IconButton(
                        onClick = { coroutine.launch { drawerState.drawerState.open() } }
                    ) {
                        Icon(Icons.Default.Menu,"")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
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
}