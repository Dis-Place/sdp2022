package com.github.displace.sdp2022.util.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

class Connectivity {
    companion object {
        @Suppress("DEPRECATED") //We are checking for the version number
        fun isConnected(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val status =
                (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && connectivityManager.activeNetwork != null)
                        && (
                        (connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null)
                                || (connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting))
            return status
        }
    }
}