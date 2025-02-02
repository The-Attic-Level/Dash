package com.the_attic_level.dash.sys.rest

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import com.the_attic_level.dash.app.Dash
import com.the_attic_level.dash.sys.Logger

class NetworkUtil
{
    // ----------------------------------------
    // Type
    
    enum class Type {
        NONE, WIFI, CELLULAR, WIFI_AND_CELLULAR
    }
    
    // ----------------------------------------
    // Callback
    
    private class Callback: ConnectivityManager.NetworkCallback()
    {
        var available  = false; private set
        var notMetered = false; private set
        
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            this.available = true
        }
        
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, capabilities)
            this.notMetered = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }
        
        override fun onLost(network: Network) {
            super.onLost(network)
            this.available = false
        }
    }
    
    // ----------------------------------------
    // Static
    
    companion object
    {
        // ----------------------------------------
        // Network Callback
        
        private var callback: Callback? = null
    
        // ----------------------------------------
        // System Services
        
        private val connectivityManager: ConnectivityManager? by lazy {
            Dash.getService(ConnectivityManager::class.java)
        }
        
        private val wifiManager: WifiManager? by lazy {
            Dash.getService(WifiManager::class.java)
        }
        
        // ----------------------------------------
        // Properties
        
        val isNetworkAvailable : Boolean
            get() = this.callback?.available ?: false
        
        val isNetworkNotMetered : Boolean
            get() = this.callback?.notMetered ?: false
        
        // ----------------------------------------
        // Methods
        
        fun request(type: Type)
        {
            if (type == Type.NONE) {
                return
            }
            
            val manager = this.connectivityManager ?: return
            val builder = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            
            if (type == Type.WIFI || type == Type.WIFI_AND_CELLULAR) {
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            }
            
            if (type == Type.CELLULAR || type == Type.WIFI_AND_CELLULAR) {
                builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
            
            try {
                val callback = Callback()
                manager.requestNetwork(builder.build(), callback)
                this.callback = callback
            } catch (e: Exception) {
                Logger.error(this, "unable to request network changes: ${e.message}")
            }
        }
        
        // ----------------------------------------
        // Wifi
        
        val supportsEnableWifi: Boolean; get() {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        }
        
        val isWifiEnabled: Boolean; get() {
            return this.wifiManager?.isWifiEnabled ?: false
        }
        
        fun enableWifi() {
            @Suppress("DEPRECATION")
            this.wifiManager?.isWifiEnabled = true
        }
    }
}