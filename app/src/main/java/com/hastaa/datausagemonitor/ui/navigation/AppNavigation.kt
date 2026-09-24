package com.hastaa.datausagemonitor.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hastaa.datausagemonitor.ui.apps.AppsScreen
import com.hastaa.datausagemonitor.ui.dashboard.DashboardUiState
import com.hastaa.datausagemonitor.ui.dashboard.DashboardViewModel
import com.hastaa.datausagemonitor.ui.home.HomeScreen
import com.hastaa.datausagemonitor.ui.settings.SettingsScreen
import com.hastaa.datausagemonitor.ui.theme.AccentPrimary
import com.hastaa.datausagemonitor.ui.theme.DarkBackground
import com.hastaa.datausagemonitor.ui.theme.DarkSurface
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceBorder
import com.hastaa.datausagemonitor.ui.theme.DarkSurfaceElevated
import com.hastaa.datausagemonitor.ui.theme.TextPrimary
import com.hastaa.datausagemonitor.ui.theme.TextSecondary

enum class NavTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Rounded.Home),
    APPS("Apps", Icons.Rounded.Apps),
    SETTINGS("Settings", Icons.Rounded.Settings)
}

@Composable
fun MainAppNavigation(
    viewModel: DashboardViewModel,
    state: DashboardUiState,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(NavTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .drawBehind {
                        // Subtle top border line for glass/dock feel
                        drawLine(
                            color = DarkSurfaceBorder,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                NavTab.values().forEach { tab ->
                    val isSelected = tab == selectedTab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentPrimary,
                            selectedTextColor = AccentPrimary,
                            indicatorColor = AccentPrimary.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        },
        containerColor = DarkBackground,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tabTransition"
            ) { targetTab ->
                when (targetTab) {
                    NavTab.HOME -> {
                        HomeScreen(
                            state = state,
                            onNavigateToApps = { selectedTab = NavTab.APPS },
                            onDismissTileBanner = viewModel::dismissTileBanner,
                            onRefresh = viewModel::refresh
                        )
                    }
                    NavTab.APPS -> {
                        AppsScreen(
                            state = state,
                            onPeriodSelected = viewModel::setPeriod,
                            onSearchQueryChanged = viewModel::setSearchQuery,
                            onNetworkFilterChanged = viewModel::setNetworkFilter
                        )
                    }
                    NavTab.SETTINGS -> {
                        SettingsScreen(
                            state = state,
                            onToggleDemoMode = viewModel::toggleDemoMode
                        )
                    }
                }
            }
        }
    }
}
