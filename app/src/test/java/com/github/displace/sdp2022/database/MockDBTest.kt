package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.database.DatabaseFactory.MOCK_DB
import com.github.displace.sdp2022.util.listeners.Listener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.lang.UnsupportedOperationException

class MockDBTest {

    @After
    @Before
    fun clear_db() {
        DatabaseFactory.clearMockDB()
    }

    private fun <T> putPrimitiveAndGetTest(reference: String, value: T){
        MOCK_DB.update(reference,value)
        var callbackCalled = false
        MOCK_DB.getThenCall<T>(reference) {
            assertEquals(it, value)
            callbackCalled = true
        }
        // mock db is synchronous so this works
        assertEquals(true, callbackCalled)
    }

    private fun childExists(reference: String){
        var listenerCalled = false
        MOCK_DB.getThenCall<Any>(reference) {
            listenerCalled = true
        }
        // mock db is synchronous so this works
        assertEquals(true, listenerCalled)
    }

    private fun <V> checkMapWithPrimitiveContents(expectedMap: Map<String,V>, actualMap: Map<String,V>?) {
        assertEquals(expectedMap.size,actualMap?.size)
        for(k in expectedMap.keys){
            assertEquals(true, actualMap?.containsKey(k))
            assertEquals(expectedMap[k],actualMap?.get(k))
        }
    }

    private fun <V> checkListWithPrimitiveContents(expectedList: List<V>, actualList: List<V>?) {
        assertEquals(expectedList.size,actualList?.size)
        for(v in expectedList){
            assertEquals(true, actualList?.contains(v))
        }
    }

    private fun <V> putMapWithPrimitiveValuesAndGetTest(reference: String, map: Map<String,V>){
        MOCK_DB.update(reference,map)
        var callbackCalled = false
        MOCK_DB.getThenCall<Map<String,Any>>(reference) {
            checkMapWithPrimitiveContents(map,it)
            callbackCalled = true
        }
        assertEquals(true, callbackCalled)

        for(k in map.keys) {
            childExists("$reference/$k")
        }
    }

    @Test
    fun puttingBooleanInNewChildWorks() {
        val folderPath = "folder"
        putPrimitiveAndGetTest("$folderPath/bool",true)
        childExists(folderPath)
    }

    @Test
    fun puttingMapAtRootWorks() {
        putMapWithPrimitiveValuesAndGetTest("map", mapOf(Pair("value",0)))
    }

    @Test
    fun puttingBooleanAtRootWorks() {
        putPrimitiveAndGetTest("bool",true)
    }

    @Test
    fun puttingIntAtRootWorks() {
        putPrimitiveAndGetTest("integer",3)
    }

    @Test
    fun puttingLongAtRootWorks() {
        putPrimitiveAndGetTest("integer",8L)
    }

    @Test
    fun puttingDoubleAtRootWorks() {
        putPrimitiveAndGetTest("integer",4.0)
    }

    @Test
    fun puttingStringAtRootWorks() {
        putPrimitiveAndGetTest("string","hello")
    }

    @Test
    fun updatePropagatesMap() {
        val first = "first"
        val second = "second"
        val third = "third"
        putPrimitiveAndGetTest("$first/$second/$third", 14)
        var firstListenerCalled = false
        var secondListenerCalled = false
        var thirdListenerCalled = false

        MOCK_DB.addListener<Any>(first) {
            firstListenerCalled = !firstListenerCalled
        }

        MOCK_DB.addListener<Any>("$first/$second") {
            secondListenerCalled = !secondListenerCalled
        }

        MOCK_DB.addListener<Any>("$first/$second/$third") {
            thirdListenerCalled = !thirdListenerCalled
        }

        MOCK_DB.update("$first/$second/$third", 15)

        assertEquals(true, firstListenerCalled)
        assertEquals(true, secondListenerCalled)
        assertEquals(true, thirdListenerCalled)
    }

    @Test
    fun addThenRemoveListenerWorks() {
        val ref = "ref"
        var listenerCalledOnce = false

        MOCK_DB.update(ref, 23)

        val listener = Listener<Any?>{
            listenerCalledOnce = !listenerCalledOnce
        }

        MOCK_DB.addListener(ref, listener)

        MOCK_DB.update(ref, 21)

        assertEquals(true, listenerCalledOnce)

        MOCK_DB.removeListener(ref, listener)
        MOCK_DB.update(ref, 78)

        assertEquals(true, listenerCalledOnce)
    }

    @Test
    fun puttingPrimitiveListAtRootWorks() {
        val reference = "list"
        val list = listOf(45,48,53,-27)
        MOCK_DB.update(reference,list)
        var listenerCalled = false
        MOCK_DB.getThenCall<List<Int>>(reference) {
            checkListWithPrimitiveContents(list,it)
            listenerCalled = true
        }
        assertEquals(true, listenerCalled)

        for(k in list.indices) {
            childExists("$reference/$k")
        }
    }

    @Test
    fun addingChildToLeafFails() {
        val childRef = "badChild"
        val leafRef = "leaf"
        putPrimitiveAndGetTest(leafRef,4.0)

        assertThrows(UnsupportedOperationException::class.java) {
            MOCK_DB.update(
                "$leafRef/$childRef",
                true
            )
        }
    }

    @Test
    fun deleteWorksMap() {
        val mapRef = "map"
        val leafRef = "leaf"
        var callbackCalled = false
        putPrimitiveAndGetTest("$mapRef/$leafRef",4.0)
        MOCK_DB.delete("$mapRef/$leafRef")
        MOCK_DB.getThenCall<Map<String,Any>>(mapRef) {
            checkMapWithPrimitiveContents(mapOf(), it)
            callbackCalled = true
        }
        assertEquals(true, callbackCalled)
    }

    @Test
    fun deleteWorksList() {
        val listRef = "list"
        val list = listOf("value")
        MOCK_DB.update(listRef, list)

        var listenerCalled = false
        MOCK_DB.addListener<List<Any>>(listRef) {
            checkListWithPrimitiveContents(listOf(), it)
            listenerCalled = true
        }
        MOCK_DB.delete("$listRef/0")
        assertEquals(true, listenerCalled)
    }






}