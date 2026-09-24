package com.hastaa.datausagemonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.hastaa.datausagemonitor.ui.dashboard.DashboardViewModel
import com.hastaa.datausagemonitor.ui.navigation.MainAppNavigation
import com.hastaa.datausagemonitor.ui.screen.UsageAccessScreen
import com.hastaa.datausagemonitor.ui.theme.DarkBackground
import com.hastaa.datausagemonitor.ui.theme.DataUsageMonitorTheme

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DataUsageMonitorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val state by viewModel.uiState.collectAsState()

                    if (!state.hasUsageAccess && !state.isDemoMode) {
                        UsageAccessScreen(
                            onPermissionGranted = {
                                viewModel.checkPermissionAndLoad()
                            },
                            onSkipToDemo = {
                                viewModel.setDemoMode(true)
                            }
                        )
                    } else {
                        MainAppNavigation(
                            viewModel = viewModel,
                            state = state
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-evaluate permission when returning from Android Settings
        viewModel.checkPermissionAndLoad()
    }
}
