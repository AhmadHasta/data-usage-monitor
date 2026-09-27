package com.hastaa.datausagemonitor

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hastaa.datausagemonitor.data.local.AppTheme
import com.hastaa.datausagemonitor.data.local.ThemePreferences
import com.hastaa.datausagemonitor.ui.screen.dashboard.DashboardScreen
import com.hastaa.datausagemonitor.ui.screen.dashboard.DashboardViewModel
import com.hastaa.datausagemonitor.ui.screen.settings.TileSettingsScreen
import com.hastaa.datausagemonitor.ui.screen.usageaccess.UsageAccessScreen
import com.hastaa.datausagemonitor.ui.theme.DataUsageMonitorTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

enum class AppScreen {
    DASHBOARD,
    TILE_SETTINGS
}

class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_FROM_QS_TILE = "com.hastaa.datausagemonitor.EXTRA_FROM_QS_TILE"
    }

    private val viewModel: DashboardViewModel by viewModels()
    private val themePreferences by lazy { ThemePreferences(this) }
    private val isQsTileSplashActive = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (intent?.getBooleanExtra(EXTRA_FROM_QS_TILE, false) == true) {
            isQsTileSplashActive.value = true
        }

        val splashStartTime = SystemClock.uptimeMillis()
        val minSplashDurationMs = 1000L

        splashScreen.setKeepOnScreenCondition {
            val elapsed = SystemClock.uptimeMillis() - splashStartTime
            viewModel.uiState.value.isLoading || elapsed < minSplashDurationMs
        }

        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashView = splashScreenViewProvider.view
            val fadeOut = ObjectAnimator.ofFloat(splashView, View.ALPHA, 1f, 0f).apply {
                interpolator = PathInterpolator(0.4f, 0f, 0.2f, 1f)
                duration = 400L
                doOnEnd {
                    splashScreenViewProvider.remove()
                }
            }
            fadeOut.start()
        }

        enableEdgeToEdge()

        val initialTheme = themePreferences.getInitialTheme()

        setContent {
            val appTheme by themePreferences.appThemeFlow.collectAsState(initial = initialTheme)
            val showQsSplash by isQsTileSplashActive

            DataUsageMonitorTheme(appTheme = appTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val state by viewModel.uiState.collectAsState()
                    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.DASHBOARD) }

                    LaunchedEffect(showQsSplash) {
                        if (showQsSplash) {
                            currentScreen = AppScreen.DASHBOARD
                            viewModel.checkPermissionAndLoad()
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        if (!state.hasUsageAccess) {
                            UsageAccessScreen(
                                onPermissionGranted = {
                                    viewModel.checkPermissionAndLoad()
                                },
                                appTheme = appTheme
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

                        if (showQsSplash) {
                            BackHandler {}
                            SplashScreenOverlay(
                                onSplashFinished = {
                                    isQsTileSplashActive.value = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_FROM_QS_TILE, false)) {
            isQsTileSplashActive.value = true
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-evaluate permission when returning from Android Settings
        viewModel.checkPermissionAndLoad()
    }
}

@Suppress("FunctionName")
@Composable
private fun SplashScreenOverlay(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var alpha by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        delay(1.seconds)
        animate(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 400,
                easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
            )
        ) { value, _ ->
            alpha = value
        }
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha }
            .background(colorResource(R.color.splash_background)),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                ImageView(ctx).apply {
                    val avd = ContextCompat.getDrawable(ctx, R.drawable.avd_anim_launcher)
                    setImageDrawable(avd)
                    (avd as? Animatable)?.start()
                }
            },
            modifier = Modifier.size(160.dp)
        )
    }
}
