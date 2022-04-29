package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.util.listeners.Listener

/**
 * DB interface which abstracts over FireDB foor more convenient structure and mocking
 */
interface GoodDB {
    /**
     * update child with value
     * @param reference
     * @param key
     * @param value
     * @return value
     */
    fun <T> update(reference: String, key: String, value: T): T

    /**
     * delete child
     * @param reference
     * @param key
     */
    fun delete(reference: String, key: String)

    /**
     * retrieve data and call callback (async)
     * @param callback
     * @param defaultValue used if value in database is null
     * @param reference
     * @param key if not null, retrieves child value, otherwise retrieves reference value
     */
    fun <T> getThenCall(callback: OnDataRetrievedCallBack<T>, defaultValue: T, reference: String, key: String? = null)

    /**
     * retrieve data and do transaction (async)
     * @param transactionSpecification
     * @param reference
     * @param key if not null, retrieves child value, otherwise retrieves reference value
     */
    fun <T> getThenDoTransaction(transactionSpecification: TransactionSpecification<T>, reference: String, key: String? = null)

    /**
     * adds a listener, call when the data is updated
     * @param listener
     * @param defaultValue used if value in database is null
     * @param reference
     * @param key if not null, retrieves child value, otherwise retrieves reference value
     */
    fun <T> addListener(listener: Listener<T>, defaultValue: T, reference: String, key: String? = null)

    /**
     * removes a listener on the data
     * @param listener
     * @param defaultValue used if value in database is null
     * @param reference
     * @param key if not null, corresponds to child value, otherwise corresponds to reference value
     */
    fun <T> removeListener(listener: Listener<T>, defaultValue: T, reference: String, key: String? = null)
}