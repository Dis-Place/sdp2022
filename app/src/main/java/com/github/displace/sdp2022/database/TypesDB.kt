package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.util.listeners.Listener

typealias OnDataRetrievedCallBack<T> = Listener<T>

fun interface OnTransactionCompleteCallback {
    fun invoke(commited: Boolean)
}