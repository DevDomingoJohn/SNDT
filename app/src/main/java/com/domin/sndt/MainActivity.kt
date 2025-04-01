package com.domin.sndt

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.domin.sndt.core.components.NavigationDrawer
import com.domin.sndt.info.InfoScreen
import com.domin.sndt.scan.ScanScreen
import com.domin.sndt.ui.theme.SNDTTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SNDTTheme {
                NavigationDrawer { drawerState, navController ->
                    NavHost(
                        navController = navController,
                        startDestination = InfoScreen
                    ) {
                        composable<InfoScreen> {
                            requestReadPhoneState()
                            InfoScreen(drawerState = drawerState)
                        }
                        composable<ScanScreen> {
                            ScanScreen(drawerState = drawerState)
                        }
                    }
                }
            }
        }
    }

    private fun requestReadPhoneState() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_PHONE_STATE),123)
        }
    }

}

@Serializable
data object InfoScreen

@Serializable
data object ScanScreen

@Serializable
data object ToolsScreen