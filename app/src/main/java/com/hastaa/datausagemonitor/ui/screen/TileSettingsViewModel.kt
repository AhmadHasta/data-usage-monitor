package com.hastaa.datausagemonitor.ui.screen

import android.app.Application
import android.content.ComponentName
import android.service.quicksettings.TileService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hastaa.datausagemonitor.data.local.TileConfig
import com.hastaa.datausagemonitor.data.local.TileContentStyle
import com.hastaa.datausagemonitor.data.local.TileIconChoice
import com.hastaa.datausagemonitor.data.local.TilePreferences
import com.hastaa.datausagemonitor.data.local.TileTextLayout
import com.hastaa.datausagemonitor.data.repository.NetworkUsageRepository
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import com.hastaa.datausagemonitor.tile.DataUsageTileService
import com.hastaa.datausagemonitor.util.ActiveNetworkType
import com.hastaa.datausagemonitor.util.NetworkTypeHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TileSettingsUiState(
    val config: TileConfig = TileConfig(),
    val activeNetwork: ActiveNetworkType = ActiveNetworkType.WIFI,
    val previewBytes: Long = 7L * 1024 * 1024 * 1024 + 840L * 1024 * 1024
)

class TileSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val tilePrefs = TilePreferences(application)
    private val repository = NetworkUsageRepository(application)

    private val _uiState = MutableStateFlow(TileSettingsUiState())
    val uiState: StateFlow<TileSettingsUiState> = _uiState.asStateFlow()

    private var saveJob: Job? = null
    private var tileUpdateJob: Job? = null

    init {
        viewModelScope.launch {
            val net = NetworkTypeHelper.getActiveNetworkType(getApplication())
            val summary = try {
                repository.getDeviceSummary(UsagePeriod.TODAY)
            } catch (e: Exception) {
                null
            }
            val bytes = summary?.let {
                when (net) {
                    ActiveNetworkType.WIFI -> it.wifiBytes
                    ActiveNetworkType.MOBILE, ActiveNetworkType.OFFLINE -> it.mobileBytes
                }
            }?.takeIf { it > 0L } ?: (7L * 1024 * 1024 * 1024 + 840L * 1024 * 1024)

            _uiState.update {
                it.copy(activeNetwork = net, previewBytes = bytes)
            }
        }

        viewModelScope.launch {
            val initialConfig = tilePrefs.getTileConfig()
            _uiState.update { it.copy(config = initialConfig) }
        }
    }

    private fun mutateConfig(debounceSave: Boolean = false, transform: (TileConfig) -> TileConfig) {
        val updatedConfig = transform(_uiState.value.config)
        _uiState.update { it.copy(config = updatedConfig) }

        if (debounceSave) {
            saveJob?.cancel()
            saveJob = viewModelScope.launch {
                delay(120)
                tilePrefs.updateConfig(updatedConfig)
                scheduleTileUpdate()
            }
        } else {
            saveJob?.cancel()
            viewModelScope.launch {
                tilePrefs.updateConfig(updatedConfig)
                scheduleTileUpdate()
            }
        }
    }

    fun setContentStyle(style: TileContentStyle) {
        mutateConfig(debounceSave = false) { it.copy(contentStyle = style) }
    }

    fun setIconChoice(choice: TileIconChoice) {
        mutateConfig(debounceSave = false) { it.copy(iconChoice = choice) }
    }

    fun setTextLayout(layout: TileTextLayout) {
        mutateConfig(debounceSave = false) { it.copy(textLayout = layout) }
    }

    fun setPeriod(period: UsagePeriod) {
        mutateConfig(debounceSave = false) { it.copy(period = period) }
    }

    fun setQuotaLimit(limitGb: Int) {
        mutateConfig(debounceSave = false) { it.copy(quotaLimitGigaBytes = limitGb) }
    }

    fun setMetricIconSize(size: Int) {
        mutateConfig(debounceSave = true) { it.copy(metricIconSizeDp = size) }
    }

    fun setMetricValueTextSize(size: Int) {
        mutateConfig(debounceSave = true) { it.copy(metricValueTextSizeSp = size) }
    }

    fun setMetricUnitTextSize(size: Int) {
        mutateConfig(debounceSave = true) { it.copy(metricUnitTextSizeSp = size) }
    }

    fun setMetricSpacing(spacing: Int) {
        mutateConfig(debounceSave = true) { it.copy(metricSpacingDp = spacing) }
    }

    fun setRingDiameter(diameter: Int) {
        mutateConfig(debounceSave = true) { it.copy(ringDiameterDp = diameter) }
    }

    fun setRingStrokeWidth(width: Int) {
        mutateConfig(debounceSave = true) { it.copy(ringStrokeWidthDp = width) }
    }

    fun setRingSweepAngle(angle: Int) {
        mutateConfig(debounceSave = true) { it.copy(ringSweepAngleDeg = angle) }
    }

    fun setProgressIconSize(size: Int) {
        mutateConfig(debounceSave = true) { it.copy(progressIconSizeDp = size) }
    }

    fun setProgressRingDiameter(diameter: Int) {
        mutateConfig(debounceSave = true) { it.copy(progressRingDiameterDp = diameter) }
    }

    fun setProgressRingStroke(stroke: Int) {
        mutateConfig(debounceSave = true) { it.copy(progressRingStrokeDp = stroke) }
    }

    fun setIconOnlySize(size: Int) {
        mutateConfig(debounceSave = true) { it.copy(iconOnlySizeDp = size) }
    }

    fun setIconStrokeWidth(stroke: Int) {
        mutateConfig(debounceSave = true) { it.copy(iconStrokeWidthDp = stroke) }
    }

    fun resetStyleSizing(style: TileContentStyle) {
        mutateConfig(debounceSave = false) { current ->
            when (style) {
                TileContentStyle.METRIC_WITH_ICON -> current.copy(
                    metricIconSizeDp = 16,
                    metricValueTextSizeSp = 20,
                    metricUnitTextSizeSp = 11,
                    metricSpacingDp = 2,
                    iconStrokeWidthDp = 3
                )
                TileContentStyle.PROGRESS_RING -> current.copy(
                    ringDiameterDp = 54,
                    ringStrokeWidthDp = 8,
                    ringSweepAngleDeg = 270
                )
                TileContentStyle.PROGRESS_WITH_ICON -> current.copy(
                    progressIconSizeDp = 24,
                    progressRingDiameterDp = 60,
                    progressRingStrokeDp = 5,
                    iconStrokeWidthDp = 3
                )
                TileContentStyle.ICON_ONLY -> current.copy(
                    iconOnlySizeDp = 38,
                    iconStrokeWidthDp = 3
                )
            }
        }
    }

    private fun scheduleTileUpdate() {
        tileUpdateJob?.cancel()
        tileUpdateJob = viewModelScope.launch {
            delay(150)
            requestTileUpdate()
        }
    }

    private fun requestTileUpdate() {
        val app = getApplication<Application>()
        try {
            TileService.requestListeningState(app, ComponentName(app, DataUsageTileService::class.java))
        } catch (ignored: Exception) {}
    }
}
