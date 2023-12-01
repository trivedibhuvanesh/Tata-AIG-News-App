package com.example.tataaignewsapp

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL


suspend fun isInternetOn(context: Context): Boolean {
    if (isMobileOrWifiConnectivityAvailable(context)) {
        try {
            val urlc = URL("https://www.google.com").openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Test")
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = 10000
            urlc.connect()
            return urlc.responseCode == 200
        } catch (e: Exception) {
            Log.d("NetworkConnectivity:","Couldn't check internet connection Exception is : $e")
            return false
        }
    } else {
        Log.d("NetworkConnectivity:","Internet not available!")
    }
    return false
}


fun isMobileOrWifiConnectivityAvailable(ctx: Context): Boolean {
    var haveConnectedWifi = false
    var haveConnectedMobile = false
    try {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI", ignoreCase = true)) if (ni.isConnected) {
                haveConnectedWifi = true
            }
            if (ni.typeName.equals("MOBILE", ignoreCase = true)) if (ni.isConnected) {
                haveConnectedMobile = true
            }
        }
    } catch (e: Exception) {
        Log.d("NetworkConnectivity:","[ConnectionVerifier] inside isInternetOn() Exception is : $e")
    }
    return haveConnectedWifi || haveConnectedMobile
}
