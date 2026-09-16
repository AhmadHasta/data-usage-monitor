package com.hastaa.datausagemonitor.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hastaa.datausagemonitor.domain.model.NetworkUsageSummary
import com.hastaa.datausagemonitor.domain.model.UsagePeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "usage_cache")

class UsageCache(private val context: Context) {

    companion object {
        private val KEY_TODAY_MOBILE_BYTES = longPreferencesKey("today_mobile_bytes")
        private val KEY_TODAY_WIFI_BYTES = longPreferencesKey("today_wifi_bytes")
        private val KEY_TODAY_TOTAL_BYTES = longPreferencesKey("today_total_bytes")
        private val KEY_LAST_UPDATED = longPreferencesKey("last_updated_millis")
        private val KEY_DISMISSED_TILE_PROMPT = booleanPreferencesKey("dismissed_tile_prompt")
    }

    val todayUsageSummaryFlow: Flow<NetworkUsageSummary> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            NetworkUsageSummary(
                mobileBytes = preferences[KEY_TODAY_MOBILE_BYTES] ?: 0L,
                wifiBytes = preferences[KEY_TODAY_WIFI_BYTES] ?: 0L,
                period = UsagePeriod.TODAY,
                lastUpdatedMillis = preferences[KEY_LAST_UPDATED] ?: 0L
            )
        }

    val dismissedTilePromptFlow: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[KEY_DISMISSED_TILE_PROMPT] ?: false }

    suspend fun saveTodayUsage(mobileBytes: Long, wifiBytes: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TODAY_MOBILE_BYTES] = mobileBytes
            preferences[KEY_TODAY_WIFI_BYTES] = wifiBytes
            preferences[KEY_TODAY_TOTAL_BYTES] = mobileBytes + wifiBytes
            preferences[KEY_LAST_UPDATED] = System.currentTimeMillis()
        }
    }

    suspend fun getTodayUsage(): NetworkUsageSummary {
        return try {
            todayUsageSummaryFlow.first()
        } catch (e: Exception) {
            NetworkUsageSummary(period = UsagePeriod.TODAY)
        }
    }

    suspend fun setDismissedTilePrompt(dismissed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DISMISSED_TILE_PROMPT] = dismissed
        }
    }
}
