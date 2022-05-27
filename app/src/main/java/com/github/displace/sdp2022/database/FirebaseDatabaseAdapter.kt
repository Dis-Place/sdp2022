package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.util.listeners.Listener
import com.google.firebase.database.*

/**
 * firebase real time database adapter to interface GoodDB
 *
 * NOTE : use factory to instantiate
 *
 * @param referenceUrl
 * @param isDebug
 *
 * @see DatabaseFactory
 * @see GoodDB
 * @author LeoLgdr
 */
class FirebaseDatabaseAdapter(referenceUrl: String, isDebug: Boolean) : GoodDB {

    private val firebaseDB = FirebaseDatabase.getInstance(referenceUrl)

    /**
     * to keep track of the ValueEventListeners references to be able to remove them
     */
    private val listenersMap = mutableMapOf<Listener<*>, ValueEventListener>()

    /**
     * to specify debug mode (prefix to all references)
     */
    private val prefix = if (isDebug) "debug/" else ""

    private fun get(reference: String): DatabaseReference {
        return firebaseDB.getReference("$prefix$reference")
    }

    override fun <T> update(reference: String, newValue: T): T {
        get(reference).setValue(newValue)
        return newValue
    }

    override fun delete(reference: String) {
        get(reference).removeValue()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getThenCall(reference: String, callback: (T?) -> Unit) {
        get(reference).get().addOnSuccessListener {
            callback(it.value as T?)
        }
    }

    override fun <T> runTransaction(reference: String, specification: TransactionSpecification<T>) {
        get(reference).runTransaction(transactionHandler(specification))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> addListener(reference: String, listener: Listener<T?>) {
        val listenerFireDB = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listener.invoke(snapshot.value as T?)
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        listenersMap[listener] = listenerFireDB

        get(reference).addValueEventListener(listenerFireDB)
    }

    override fun <T> removeListener(reference: String, listener: Listener<T?>) {
        val listenerFireDB = listenersMap[listener]
        if(listenerFireDB != null) {
            get(reference).removeEventListener(listenerFireDB)
            listenersMap.remove(listener)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> transactionHandler(specification: TransactionSpecification<T>): Transaction.Handler {
        return object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val old = currentData.value as T? ?: return Transaction.success(currentData)
                if(specification.preCheck(old)) {
                    currentData.value = specification.computeUpdatedData(old)
                    return Transaction.success(currentData)
                }
                return Transaction.abort()
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                specification.onCompleteCallback(committed)
            }
        }
    }

}