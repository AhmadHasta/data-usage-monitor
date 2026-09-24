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
import com.hastaa.datausagemonitor.ui.screen.DashboardScreen
import com.hastaa.datausagemonitor.ui.screen.UsageAccessScreen
import com.hastaa.datausagemonitor.ui.theme.DataUsageMonitorTheme

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DataUsageMonitorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val state by viewModel.uiState.collectAsState()

                    if (!state.hasUsageAccess) {
                        UsageAccessScreen(
                            onPermissionGranted = {
                                viewModel.checkPermissionAndLoad()
                            }
                        )
                    } else {
                        DashboardScreen(
                            state = state,
                            onPeriodSelected = viewModel::setPeriod,
                            onSearchQueryChanged = viewModel::setSearchQuery,
                            onNetworkFilterChanged = viewModel::setNetworkFilter,
                            onDismissTileBanner = viewModel::dismissTileBanner,
                            onRefresh = viewModel::refresh
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
