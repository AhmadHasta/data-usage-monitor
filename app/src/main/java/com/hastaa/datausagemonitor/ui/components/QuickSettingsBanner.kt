package com.hastaa.datausagemonitor.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.hastaa.datausagemonitor.data.local.AppTheme
import com.hastaa.datausagemonitor.ui.components.banner.CyberNeonQuickSettingsBanner
import com.hastaa.datausagemonitor.ui.components.banner.M3QuickSettingsBanner
import com.hastaa.datausagemonitor.ui.theme.LocalAppTheme
import com.hastaa.datausagemonitor.util.QuickSettingsTileRequester

@Composable
fun QuickSettingsBanner(
    onDismiss: () -> Unit,
    onCustomizeTile: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appTheme = LocalAppTheme.current
    val isCyberNeon = appTheme == AppTheme.CYBER_NEON

    val onAddTileAction = {
        QuickSettingsTileRequester.requestAddTile(context) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        onDismiss()
    }

    if (isCyberNeon) {
        CyberNeonQuickSettingsBanner(
            onDismiss = onDismiss,
            onAddTile = onAddTileAction,
            onCustomizeTile = onCustomizeTile,
            modifier = modifier
        )
    } else {
        M3QuickSettingsBanner(
            onDismiss = onDismiss,
            onAddTile = onAddTileAction,
            onCustomizeTile = onCustomizeTile,
            modifier = modifier
        )
    }
}
