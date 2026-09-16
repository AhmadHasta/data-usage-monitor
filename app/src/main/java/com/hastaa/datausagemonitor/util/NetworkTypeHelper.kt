package com.hastaa.datausagemonitor.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

enum class ActiveNetworkType(val label: String) {
    MOBILE("Mobile"),
    WIFI("Wi-Fi"),
    OFFLINE("Offline")
}

object NetworkTypeHelper {

    /**
     * Determines the currently active network connection type (Wi-Fi, Mobile Data, or Offline).
     */
    fun getActiveNetworkType(context: Context): ActiveNetworkType {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return ActiveNetworkType.OFFLINE
        val activeNetwork = cm.activeNetwork ?: return ActiveNetworkType.OFFLINE
        val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return ActiveNetworkType.OFFLINE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ActiveNetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ActiveNetworkType.MOBILE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ActiveNetworkType.WIFI
            else -> ActiveNetworkType.OFFLINE
        }
    }
}
