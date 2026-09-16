package com.hastaa.datausagemonitor.ui.dashboard

import android.app.Application
import android.content.ComponentName
import android.os.Build
import android.service.quicksettings.TileService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hastaa.datausagemonitor.data.repository.NetworkUsageRepository
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.tile.DataUsageTileService
import com.hastaa.datausagemonitor.util.PermissionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NetworkUsageRepository(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeTileBannerDismissal()
        checkPermissionAndLoad()
    }

    private fun observeTileBannerDismissal() {
        viewModelScope.launch {
            val dismissed = repository.dismissedTilePromptFlow.first()
            val canShow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !dismissed
            _uiState.update { it.copy(showTileBanner = canShow) }
        }
    }

    fun checkPermissionAndLoad() {
        val context = getApplication<Application>()
        val hasPermission = PermissionHelper.hasUsageAccess(context)

        _uiState.update { it.copy(hasUsageAccess = hasPermission) }

        if (hasPermission) {
            loadUsageData()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setPeriod(period: UsagePeriod) {
        if (_uiState.value.period == period) return
        _uiState.update { it.copy(period = period) }
        loadUsageData()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setNetworkFilter(filter: NetworkFilter) {
        _uiState.update { it.copy(networkFilter = filter) }
    }

    fun dismissTileBanner() {
        viewModelScope.launch {
            repository.setDismissedTilePrompt(true)
            _uiState.update { it.copy(showTileBanner = false) }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadUsageData(isRefresh = true)
    }

    private fun loadUsageData(isRefresh: Boolean = false) {
        val period = _uiState.value.period
        viewModelScope.launch {
            if (!isRefresh) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            try {
                // Fetch summary and apps concurrently in repository
                val summary = repository.getDeviceSummary(period)
                val apps = repository.getAppUsage(period)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        mobileBytes = summary.mobileBytes,
                        wifiBytes = summary.wifiBytes,
                        apps = apps,
                        error = null
                    )
                }

                // Notify Quick Settings Tile to update if period was TODAY
                if (period == UsagePeriod.TODAY) {
                    notifyTileUpdate()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "Unable to load data usage: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    private fun notifyTileUpdate() {
        try {
            val context = getApplication<Application>()
            val component = ComponentName(context, DataUsageTileService::class.java)
            TileService.requestListeningState(context, component)
        } catch (e: Exception) {
            // Ignored on platforms or states where tile is not active
        }
    }
}
