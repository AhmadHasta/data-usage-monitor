package com.hastaa.datausagemonitor.domain.model

enum class TileContentStyle(val label: String, val subtitle: String) {
    METRIC_WITH_ICON(
        label = "Numbers & Icon",
        subtitle = "Display usage metric, network icon, or both"
    ),
    PROGRESS_RING(
        label = "Progress Ring",
        subtitle = "Progress ring arc and quota gauge with dynamic sweep angle"
    ),
    PROGRESS_WITH_ICON(
        label = "Ring & Icon",
        subtitle = "Progress ring enclosing centered network icon"
    ),
    ICON_ONLY(
        label = "Icon Only",
        subtitle = "Minimalist network icon centered in tile"
    )
}

enum class TileIconChoice(val label: String, val subtitle: String) {
    AUTO(
        label = "Auto (Active Network)",
        subtitle = "Wi-Fi when connected to Wi-Fi, Cellular on mobile data"
    ),
    WIFI(
        label = "Always Wi-Fi",
        subtitle = "Always show Wi-Fi wave symbol"
    ),
    CELLULAR(
        label = "Always Cellular",
        subtitle = "Always show cellular signal bars"
    ),
    DATA_USAGE(
        label = "Data Usage",
        subtitle = "Show dual upload/download data arrows"
    )
}

enum class TileTextLayout(
    val label: String,
    val subtitle: String
) {
    SINGLE_LINE(
        label = "Single Line (Value + Network)",
        subtitle = "Numbers and network name together in one line"
    ),
    DUAL_LINE_NETWORK_FIRST(
        label = "Two Lines (Network on Top)",
        subtitle = "Network name on the first line, usage numbers below"
    ),
    DUAL_LINE_METRIC_FIRST(
        label = "Two Lines (Value on Top)",
        subtitle = "Usage numbers on the first line, network and period below"
    ),
    METRIC_ONLY(
        label = "Numbers Only",
        subtitle = "Usage metric only, without Wi-Fi or Mobile label"
    ),
    NETWORK_ONLY(
        label = "Network Only",
        subtitle = "Network label only, without usage numbers"
    );

    val isBoth: Boolean get() = this == SINGLE_LINE || this == DUAL_LINE_NETWORK_FIRST || this == DUAL_LINE_METRIC_FIRST
}

enum class MetricDisplayMode(val label: String, val subtitle: String) {
    NUMBERS_AND_ICON(
        label = "Numbers & Icon",
        subtitle = "Usage metric, unit (GB/MB), and network icon above"
    ),
    TEXT_ONLY(
        label = "Text Only",
        subtitle = "Usage metric and unit only without icon"
    ),
    ICON_ONLY(
        label = "Icon Only",
        subtitle = "Minimalist network icon centered in tile"
    )
}

data class TileConfig(
    val contentStyle: TileContentStyle = TileContentStyle.METRIC_WITH_ICON,
    val metricDisplayMode: MetricDisplayMode = MetricDisplayMode.NUMBERS_AND_ICON,
    val iconChoice: TileIconChoice = TileIconChoice.AUTO,
    val textLayout: TileTextLayout = TileTextLayout.SINGLE_LINE,
    val period: UsagePeriod = UsagePeriod.TODAY,
    val quotaLimitGigaBytes: Int = 10,
    val metricIconSizeDp: Int = 16,
    val metricValueTextSizeSp: Int = 20,
    val metricUnitTextSizeSp: Int = 11,
    val metricSpacingDp: Int = 2,
    val ringDiameterDp: Int = 54,
    val ringStrokeWidthDp: Int = 8,
    val ringSweepAngleDeg: Int = 270,
    val progressIconSizeDp: Int = 24,
    val progressRingDiameterDp: Int = 60,
    val progressRingStrokeDp: Int = 5,
    val iconOnlySizeDp: Int = 38,
    val iconStrokeWidthDp: Int = 3
) {
    val hasIcon: Boolean
        get() = when (contentStyle) {
            TileContentStyle.METRIC_WITH_ICON -> metricDisplayMode != MetricDisplayMode.TEXT_ONLY
            TileContentStyle.PROGRESS_RING -> false
            TileContentStyle.PROGRESS_WITH_ICON -> true
            TileContentStyle.ICON_ONLY -> true
        }

    val hasUsageData: Boolean
        get() = when (contentStyle) {
            TileContentStyle.PROGRESS_RING,
            TileContentStyle.PROGRESS_WITH_ICON -> true
            TileContentStyle.METRIC_WITH_ICON -> {
                !(metricDisplayMode == MetricDisplayMode.ICON_ONLY && textLayout == TileTextLayout.NETWORK_ONLY)
            }
            TileContentStyle.ICON_ONLY -> textLayout != TileTextLayout.NETWORK_ONLY
        }
}
