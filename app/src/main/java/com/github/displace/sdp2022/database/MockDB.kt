package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.util.listeners.Listener

/**
 * mocked Database
 *
 * NOTE : use factory to instantiate
 *
 * @author LeoLgdr
 * @see DatabaseFactory
 * @see GoodDB
 */
class MockDB : GoodDB {
    override fun <T> update(reference: String, newValue: T): T {
        TODO("Not yet implemented")
    }

    override fun delete(reference: String) {
        TODO("Not yet implemented")
    }

    override fun <T> getThenCall(reference: String, callback: (T?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun <T> runTransaction(reference: String, specification: TransactionSpecification<T>) {
        TODO("Not yet implemented")
    }

    override fun <T> addListener(reference: String, listener: Listener<T?>) {
        TODO("Not yet implemented")
    }

    override fun <T> removeListener(reference: String, listener: Listener<T?>) {
        TODO("Not yet implemented")
    }
}