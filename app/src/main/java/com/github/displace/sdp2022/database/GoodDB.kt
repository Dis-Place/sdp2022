package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.util.listeners.Listener

/**
 * Database interface which abstracts over FireDB for convenient structure and mocking
 *
 * @author LeoLgdr
 */
interface GoodDB {

    /**
     * update data
     *
     * @param T data type
     * @param reference data path
     * @param newValue
     * @return updated value
     */
    fun <T> update(reference: String, newValue: T): T

    /**
     * delete data
     *
     * @param reference data path
     */
    fun delete(reference: String)

    /**
     * retrieves data and then calls the specified callback
     *
     * @param T
     * @param reference
     * @param callback
     */
    fun <T> getThenCall(reference: String, callback: (T?) -> Unit)

    /**
     * run a transaction, according to specification
     *
     * @param T data type
     * @param reference data path
     * @param specification
     *
     * @see TransactionSpecification
     */
    fun <T> runTransaction(reference: String, specification: TransactionSpecification<T>)

    /**
     * add a listener to some data
     *
     * @param T type of data
     * @param reference data path
     * @param listener called after each time the data changed
     */
    fun <T> addListener(reference: String, listener: Listener<T?>)

    /**
     * removes a listener of some data
     *
     * @param T
     * @param reference data path
     * @param listener
     */
    fun <T> removeListener(reference: String, listener: Listener<T?>)
}