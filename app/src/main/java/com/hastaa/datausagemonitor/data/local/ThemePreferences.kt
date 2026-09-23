package com.hastaa.datausagemonitor.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

enum class AppTheme(val id: String, val label: String, val subtitle: String) {
    CYBER_NEON(
        id = "cyber_neon",
        label = "Cyber Neon (Default)",
        subtitle = "Latar obsidian gelap dengan aksen Cyan & Violet neon yang tajam"
    ),
    MD3_EXPRESSIVE(
        id = "md3_expressive",
        label = "Material 3 Expressive",
        subtitle = "Palet warna tonal adaptif Material You dengan Dynamic Color"
    )
}

class ThemePreferences(private val context: Context) {

    companion object {
        private val KEY_APP_THEME = stringPreferencesKey("app_theme")
    }

    val appThemeFlow: Flow<AppTheme> = context.themeDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeStr = preferences[KEY_APP_THEME]
            runCatching { AppTheme.valueOf(themeStr ?: "") }
                .getOrDefault(AppTheme.CYBER_NEON)
        }

    suspend fun setAppTheme(theme: AppTheme) {
        context.themeDataStore.edit { preferences ->
            preferences[KEY_APP_THEME] = theme.name
        }
    }
}
