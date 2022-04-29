package com.github.displace.sdp2022.database

/**
 * to specify a transaction to the database
 * Use Builder to get a new instance
 * @author LeoLgdr
 */
class TransactionSpecification<T> private constructor(
    val onTransactionCompleteCallback: (Boolean) -> Unit,
    val abortOnNull: Boolean,
    val transactionTransform: (T) -> T,
    val preCheck: (T) -> Boolean,
    val onNull: () -> T?
) {

    /**
     * Builder for TransactionSpecification
     * only the desired specifications should be set
     * by default, callbacks & transform do nothing
     * @author LeoLgdr
     */
    class Builder<T> {

        /**
         * called after Transaction completion
         */
        var onTransactionCompleteCallback : (Boolean) -> Unit = {}

        /**
         * whether or not to abort transaction if the value found in the database is null
         * set to false by default
         */
        var abortOnNull = false

        /**
         * data transformation during transaction
         */
        var transactionTransform : (T) -> T = { it }

        /**
         * check on data before transform
         */
        var preCheck : (T) -> Boolean = { _ -> true }

        /**
         * called whenever the value in the database is null
         * and abortOnNull is false
         * by default returns null
         */
        var onNull : () -> T? = { null }

        /**
         * @return corresponding TransactionSpecification
         */
        fun build(): TransactionSpecification<T> {
            return TransactionSpecification(
                onTransactionCompleteCallback,
                abortOnNull,
                transactionTransform,
                preCheck,
                onNull
            )
        }
    }
}
