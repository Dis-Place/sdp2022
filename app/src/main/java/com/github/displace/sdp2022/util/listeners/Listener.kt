package com.github.displace.sdp2022.util.listeners

/**
 * generic lambda
 * @author LeoLgdr
 */
fun interface Listener<T> {
    fun invoke(value: T)
}