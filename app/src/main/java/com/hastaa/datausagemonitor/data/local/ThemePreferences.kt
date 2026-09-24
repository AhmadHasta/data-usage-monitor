package com.hastaa.datausagemonitor.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

enum class AppTheme(val id: String, val label: String, val subtitle: String) {
    CYBER_NEON(
        id = "cyber_neon",
        label = "Cyber Neon",
        subtitle = "Dark obsidian background with sharp Cyan & Violet neon accents"
    ),
    MD3_EXPRESSIVE(
        id = "md3_expressive",
        label = "Material 3 Expressive",
        subtitle = "Adaptive tonal color palette of Material You with Dynamic Color"
    )
}

class ThemePreferences(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "theme_preferences_cache"
        private const val KEY_APP_THEME_FAST = "app_theme"
        private val KEY_APP_THEME = stringPreferencesKey("app_theme")
    }

    private val fastPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getInitialTheme(): AppTheme {
        val cached = fastPrefs.getString(KEY_APP_THEME_FAST, null)
        if (cached != null) {
            return runCatching { AppTheme.valueOf(cached) }.getOrDefault(AppTheme.CYBER_NEON)
        }

        return try {
            runBlocking(Dispatchers.IO) {
                val dsValue = context.themeDataStore.data.first()[KEY_APP_THEME]
                val theme = dsValue?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() } ?: AppTheme.CYBER_NEON
                fastPrefs.edit().putString(KEY_APP_THEME_FAST, theme.name).apply()
                theme
            }
        } catch (_: Exception) {
            AppTheme.CYBER_NEON
        }
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
            val theme = runCatching { AppTheme.valueOf(themeStr ?: "") }
                .getOrDefault(getInitialTheme())
            fastPrefs.edit().putString(KEY_APP_THEME_FAST, theme.name).apply()
            theme
        }

    suspend fun setAppTheme(theme: AppTheme) {
        fastPrefs.edit().putString(KEY_APP_THEME_FAST, theme.name).apply()
        context.themeDataStore.edit { preferences ->
            preferences[KEY_APP_THEME] = theme.name
        }
    }
}
