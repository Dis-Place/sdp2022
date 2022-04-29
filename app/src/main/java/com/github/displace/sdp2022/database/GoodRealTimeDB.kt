package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.util.listeners.Listener
import com.google.firebase.database.*

class GoodRealTimeDB(referenceUrl: String, isDebug: Boolean) : GoodDB {

    private val firebaseDB = FirebaseDatabase.getInstance(referenceUrl)
    private val listenersMap = mutableMapOf<Listener<*>,ValueEventListener>()
    private val prefix = if (isDebug) "debug/" else ""

    private fun getRef(reference: String): DatabaseReference {
        return firebaseDB.getReference("$prefix$reference")
    }

    private fun get(reference: String, key: String? = null): DatabaseReference {
        if (key == null) {
            return getRef(reference)
        }
        return getRef(reference).child(key)
    }

    override fun <T> update(reference: String, key: String, value: T): T {
        get(reference, key).setValue(value)
        return value
    }

    override fun delete(reference: String, key: String) {
        get(reference, key).removeValue()
    }

    override fun <T> getThenCall(
        callback: OnDataRetrievedCallBack<T>,
        defaultValue: T,
        reference: String,
        key: String?
    ) {
        get(reference, key).get().addOnSuccessListener {
            val value = (it.value ?: defaultValue) as T
            callback.invoke(value)
        }
    }

    override fun <T> getThenDoTransaction(
        transactionSpecification: TransactionSpecification<T>,
        reference: String,
        key: String?
    ) {
        get(reference, key).runTransaction(transactionHandler(transactionSpecification))
    }

    override fun <T> addListener(
        listener: Listener<T>,
        defaultValue: T,
        reference: String,
        key: String?
    ) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listener.invoke((snapshot.value ?: defaultValue) as T)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        listenersMap[listener] = valueEventListener
        get(reference, key).addValueEventListener(valueEventListener)
    }

    override fun <T> removeListener(
        listener: Listener<T>,
        defaultValue: T,
        reference: String,
        key: String?
    ) {
        val valueEventListener = listenersMap[listener]
        if(valueEventListener != null){
            get(reference, key).removeEventListener(valueEventListener)
            listenersMap.remove(listener)
        }
    }

    private fun <T> transactionHandler(transactionSpecification: TransactionSpecification<T>): Transaction.Handler {
        return object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val oldUncasted = currentData.value
                if (oldUncasted == null) {
                    if (transactionSpecification.abortOnNull) {
                        return Transaction.abort()
                    }

                    currentData.value = transactionSpecification.onNull()
                    return Transaction.success(currentData)
                } else {
                    val old = oldUncasted as T
                    if (!transactionSpecification.preCheck(old)) {
                        return Transaction.abort()
                    }

                    currentData.value = transactionSpecification.transactionTransform(old)
                    return Transaction.success(currentData)
                }
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                transactionSpecification.onTransactionCompleteCallback(committed)
            }
        }
    }


}