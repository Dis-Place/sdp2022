package com.github.displace.sdp2022.util.listeners

/**
 * generic lambda
 */
fun interface Listener<T> {
    fun invoke(value: T)
}