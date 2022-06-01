package com.github.displace.sdp2022.authentication

import android.app.Activity
import com.google.android.gms.tasks.*
import java.lang.Exception
import java.util.concurrent.Executor

/**
 * implementation of a fully synchronous task,
 * of which the result is known.
 *
 * It never fails and is never cancelled
 *
 * @param TResult task result
 *
 * @author LeoLgdr
 */
class MockTask<TResult>(private val result: TResult?): Task<TResult>() {

    override fun getResult(): TResult? {
        return result
    }

    override fun isCanceled(): Boolean {
        return false
    }

    override fun isComplete(): Boolean {
        return true
    }

    override fun isSuccessful(): Boolean {
        return true
    }

    override fun addOnSuccessListener(p0: OnSuccessListener<in TResult>): Task<TResult> {
        p0.onSuccess(result)
        return this
    }

    override fun addOnCompleteListener(p0: OnCompleteListener<TResult>): Task<TResult> {
        p0.onComplete(this)
        return this
    }

    override fun addOnCanceledListener(p0: OnCanceledListener): Task<TResult> {
        return this
    }

    override fun addOnFailureListener(p0: OnFailureListener): Task<TResult> {
        return this
    }

    // task is synchronous and never cancelled, it never fails and is already complete
    // following methods implementations trivially follow from this assumption
    // or are direct calls of above methods

    override fun addOnCanceledListener(p0: Activity, p1: OnCanceledListener): Task<TResult> {
        return addOnCanceledListener(p1)
    }

    override fun addOnCanceledListener(p0: Executor, p1: OnCanceledListener): Task<TResult> {
        return addOnCanceledListener(p1)
    }

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<TResult> {
        return addOnFailureListener(p1)
    }

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<TResult> {
        return addOnFailureListener(p1)
    }

    override fun addOnSuccessListener(
        p0: Activity,
        p1: OnSuccessListener<in TResult>
    ): Task<TResult> {
        return addOnSuccessListener(p1)
    }

    override fun addOnSuccessListener(
        p0: Executor,
        p1: OnSuccessListener<in TResult>
    ): Task<TResult> {
        return addOnSuccessListener(p1)
    }

    override fun getException(): Exception? {
        return null
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): TResult? = getResult()
}