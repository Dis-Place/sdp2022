package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.util.collections.ObservableMultiLevelMap
import com.github.displace.sdp2022.util.listeners.Listener

/**
 * mock Database
 *
 * NOTE : use factory to get an instance
 *
 * @author LeoLgdr
 * @see DatabaseFactory
 * @see GoodDB
 */
class MockDB : GoodDB {
    private val contents: ObservableMultiLevelMap = ObservableMultiLevelMap()

    override fun <T> update(reference: String, newValue: T): T {
        contents.update(reference, newValue)
        return newValue
    }

    override fun delete(reference: String) {
        contents.delete(reference)
    }

    override fun <T> getThenCall(reference: String, callback: (T?) -> Unit) {
        callback(contents.get(reference) as T?)
    }

    override fun <T> runTransaction(reference: String, specification: TransactionSpecification<T>) {
        val old = contents.get(reference) as T?
        val committed = specification.preCheck(old)
        if(committed) {
            contents.update(reference, specification.computeUpdatedData(old))
        }
        specification.onCompleteCallback(committed)
    }

    override fun <T> addListener(reference: String, listener: Listener<T?>) {
        contents.addListener(reference, listener)
    }

    override fun <T> removeListener(reference: String, listener: Listener<T?>) {
        contents.removeListener(reference, listener)
    }
}