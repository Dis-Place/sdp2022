package com.github.displace.sdp2022.util.gps

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import org.osmdroid.util.GeoPoint

/**
 * entity to manage GeoPointListeners (add, remove, call all)
 * @author LeoLgdr
 */
class GeoPointListenersManager {
    private val listeners = mutableListOf<GeoPointListener>()

    fun addCall(vararg listeners: GeoPointListener){
        this.listeners.addAll(listeners)
    }

    fun removeCall(vararg listeners: GeoPointListener){
        this.listeners.removeAll(listeners)
    }

    fun clearAllCalls(){
        listeners.clear()
    }

    fun invokeAll(geoPoint: GeoPoint){
        for(l in current()){
            l.invoke(geoPoint)
        }
    }

    fun current() : List<GeoPointListener>{
        return listeners.toList()
    }
}