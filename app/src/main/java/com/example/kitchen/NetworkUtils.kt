package com.example.kitchen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


object NetworkUtils {
    private const val TYPE_WIFI = 1
    private const val TYPE_MOBILE = 2
    private const val TYPE_NOT_CONNECTED = 0

    fun getConnectivityStatus(context: Context): Int {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI
                && networkInfo.state == NetworkInfo.State.CONNECTED
            ) {
                return TYPE_WIFI
            } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE
                && networkInfo.state == NetworkInfo.State.CONNECTED
            ) {
                return TYPE_MOBILE
            }
        }
        return TYPE_NOT_CONNECTED
    }

    fun isNetworkConnected(context: Context): Boolean {
        val networkStatus = getConnectivityStatus(context)
        return if (networkStatus == TYPE_WIFI || networkStatus == TYPE_MOBILE) {
            true
        } else {
            false
        }
    }
}