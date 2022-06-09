package com.github.displace.sdp2022.util.listeners

/**
 * handles multiple generic listeners
 */
class ListenerManager<T> {
    private val listeners = mutableListOf<Listener<T>>()

    /**
     * schedules a listener to be called with each subsequent call to invokeAll
     * @param listener
     */
    fun addCall(listener: Listener<T>) {
        this.listeners.add(listener)
    }

    /**
     * schedule a listener to be called ONLY ONCE with the next invokeAll
     * @param listener
     */
    fun addCallOnce(listener: Listener<T>) {
        addCall(object : Listener<T> {
            override fun invoke(value: T) {
                listener.invoke(value)
                this@ListenerManager.removeCall(this)
            }
        })
    }

    /**
     * removes a listener
     * @param listener
     */
    fun removeCall(listener: Listener<T>) {
        this.listeners.remove(listener)
    }

    /**
     * removes all scheduled listeners
     */
    fun clearAllCalls() {
        listeners.clear()
    }

    /**
     * call all scheduled listeners
     * @param value passed as argument to all scheduled listeners
     */
    fun invokeAll(value: T) {
        for (l in listeners()) {
            l.invoke(value)
        }
    }

    /**
     * all scheduled listeners for the next call to invokeALL
     */
    fun listeners(): List<Listener<T>> {
        return listeners.toList()
    }
}