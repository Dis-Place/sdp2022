package com.github.displace.sdp2022.authentication

import org.junit.Assert.*
import org.junit.Test

class MockTaskTest {

    val result = 10
    val mockTask = MockTask(result)

    @Test
    fun testGetResult() {
        assertEquals(result, mockTask.result)
    }

    @Test
    fun isCanceled() {
        assertFalse(mockTask.isCanceled)
    }

    @Test
    fun isComplete() {
        assertTrue(mockTask.isComplete)
    }

    @Test
    fun isSuccessful() {
        assertTrue(mockTask.isSuccessful)
    }

    @Test
    fun addOnSuccessListener() {
        var callBackCalled = false
        mockTask.addOnSuccessListener { callBackCalled = true }
        assertTrue(callBackCalled)
    }

    @Test
    fun addOnCompleteListener() {
        var callBackCalled = false
        mockTask.addOnCompleteListener { callBackCalled = true }
        assertTrue(callBackCalled)
    }


    @Test
    fun testAddOnCanceledListener() {
        var callBackCalled = false
        mockTask.addOnCanceledListener { callBackCalled = true }
        assertFalse(callBackCalled)
    }


    @Test
    fun testAddOnFailureListener() {
        var callBackCalled = false
        mockTask.addOnFailureListener { callBackCalled = true }
        assertFalse(callBackCalled)
    }

    @Test
    fun testGetException() {
        assertNull(mockTask.exception)
    }
}