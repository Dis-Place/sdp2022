package com.github.displace.sdp2022.database

/**
 * specifies a transaction
 *
 * use the provided builder to instantiate
 *
 * @param T data type
 * @param preCheck check ran on the data before updating it
 * @param computeUpdatedData update data based on the old data found
 * @param onCompleteCallback called after transaction completion
 *
 * @author LeoLgdr
 * @see Builder
 * @see GoodDB
 */
class TransactionSpecification<T> private constructor(
    val preCheck: (T?) -> Boolean,
    val computeUpdatedData: (T?) -> T,
    val onCompleteCallback: (Boolean) -> Unit
) {

    /**
     * Builder for TransactionSpecification.
     * After construction, only the desired specifications should be set
     *
     * @param T data type
     * @param computeUpdatedData update data based on the old data found
     *
     * @author LeoLgdr
     * @see TransactionSpecification
     * @see GoodDB
     */
    class Builder<T>(private val computeUpdatedData : (T?) -> T) {

        /**
         * called after Transaction completion
         *
         * by default, does nothing
         */
        var onCompleteCallback: (Boolean) -> Unit = {}


        /**
         * check ran on the data before updating it
         *
         * returns true by default
         */
        var preCheck: (T?) -> Boolean = { _ -> true }

        /**
         * Set the preCheck to a new one
         */
        fun preCheckChange ( newPreCheck : (T?) -> Boolean ) : Builder<T>{
            preCheck = newPreCheck
            return this
        }

        /**
         * Set the onCompleteCallback to a new one
         */
        fun onCompleteChange ( newOnComplete : (Boolean) -> Unit ) : Builder<T>{
            onCompleteCallback = newOnComplete
            return this
        }

        /**
         * @return corresponding TransactionSpecification
         */
        fun build(): TransactionSpecification<T> {
            return TransactionSpecification(
                preCheck, computeUpdatedData, onCompleteCallback
            )
        }


    }
}
