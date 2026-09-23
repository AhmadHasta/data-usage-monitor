package com.hastaa.datausagemonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hastaa.datausagemonitor.data.local.AppTheme
import com.hastaa.datausagemonitor.data.local.ThemePreferences
import com.hastaa.datausagemonitor.ui.dashboard.DashboardViewModel
import com.hastaa.datausagemonitor.ui.screen.DashboardScreen
import com.hastaa.datausagemonitor.ui.screen.TileSettingsScreen
import com.hastaa.datausagemonitor.ui.screen.UsageAccessScreen
import com.hastaa.datausagemonitor.ui.theme.DataUsageMonitorTheme

enum class AppScreen {
    DASHBOARD,
    TILE_SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()
    private val themePreferences by lazy { ThemePreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appTheme by themePreferences.appThemeFlow.collectAsState(initial = AppTheme.CYBER_NEON)

            DataUsageMonitorTheme(appTheme = appTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val state by viewModel.uiState.collectAsState()
                    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.DASHBOARD) }

                    if (!state.hasUsageAccess) {
                        UsageAccessScreen(
                            onPermissionGranted = {
                                viewModel.checkPermissionAndLoad()
                            }
                        )
                    } else {
                        when (currentScreen) {
                            AppScreen.DASHBOARD -> {
                                DashboardScreen(
                                    state = state,
                                    onPeriodSelected = viewModel::setPeriod,
                                    onSearchQueryChanged = viewModel::setSearchQuery,
                                    onNetworkFilterChanged = viewModel::setNetworkFilter,
                                    onDismissTileBanner = viewModel::dismissTileBanner,
                                    onRefresh = viewModel::refresh,
                                    onOpenTileSettings = { currentScreen = AppScreen.TILE_SETTINGS }
                                )
                            }
                            AppScreen.TILE_SETTINGS -> {
                                BackHandler { currentScreen = AppScreen.DASHBOARD }
                                TileSettingsScreen(
                                    onBack = { currentScreen = AppScreen.DASHBOARD }
                                )
                            }
                        }
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
