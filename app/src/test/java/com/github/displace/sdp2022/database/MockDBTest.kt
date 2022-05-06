package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.database.DatabaseFactory.MOCK_DB
import com.github.displace.sdp2022.util.listeners.Listener
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.UnsupportedOperationException

class MockDBTest {

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
    fun deleteInListFailsWithUOE() {
        val listRef = "list"
        val list = listOf("value")
        MOCK_DB.update(listRef, list)

        assertThrows(UnsupportedOperationException::class.java) { MOCK_DB.delete("$listRef/0") }
    }

    @Test
    fun updateInListFailsWithUOE() {
        val listRef = "list"
        val list = listOf("value")
        MOCK_DB.update(listRef, list)
        assertThrows(UnsupportedOperationException::class.java) { MOCK_DB.update("$listRef/1","newValue") }
    }

    @Test
    fun puttingListOfMapsWork() {
        val list = listOf(mapOf(Pair("abc",-1), Pair("de",78), Pair("f",-52)), mapOf(Pair("g",32),Pair("abc",-2)))
        val ref = "list"
        var callbackCalled = false
        MOCK_DB.update(ref,list)
        MOCK_DB.getThenCall<List<Map<String,Int>>>(ref) {
            assertEquals(list.size,it?.size)
            for(i in list.indices) {
                checkMapWithPrimitiveContents(list[i],it?.get(i))
            }
            callbackCalled = true
        }
        assertEquals(true, callbackCalled)
    }

    @Test
    fun iterativeMapBuildingWorks() {
        val folder = "folder"
        val secondMapRef = "second"
        val folderMap = mapOf(Pair("first",mapOf(Pair("k",7.0))))

        val secondMap = mapOf(Pair("a",0.8),Pair("b",0.85),Pair("c",-0.7))
        val expectedFolderMap = folderMap.plus(Pair(secondMapRef,secondMap))

        val newSecondMap = mapOf(Pair("a",0.49),Pair("b",0.53),Pair("d",-0.7))

        MOCK_DB.update(folder, folderMap)
        var folderMapListenerCallsCount = 0
        MOCK_DB.update("$folder/$secondMapRef", secondMap)
        var callBackCalled = false
        MOCK_DB.getThenCall<Map<String,Map<String,Double>>>(folder) {
            assertEquals(expectedFolderMap.size, it?.size)
            for(k in expectedFolderMap.keys) {
                checkMapWithPrimitiveContents(expectedFolderMap[k]!!, it?.get(k))
            }
            callBackCalled = true
        }
        assertEquals(true, callBackCalled)
        callBackCalled = false

        MOCK_DB.addListener<Map<String,Map<String,Double>>>(folder) {
            folderMapListenerCallsCount += 1
        }

        MOCK_DB.update("$folder/$secondMapRef", newSecondMap)
        assertEquals(1, folderMapListenerCallsCount)

        val newExpectedFolderMap = folderMap.plus(Pair(secondMapRef,newSecondMap))

        MOCK_DB.getThenCall<Map<String,Map<String,Double>>>(folder) {
            assertEquals(newExpectedFolderMap.size, it?.size)
            for(k in newExpectedFolderMap.keys) {
                checkMapWithPrimitiveContents(newExpectedFolderMap[k]!!, it?.get(k))
            }
            callBackCalled = true
        }
        assertEquals(true, callBackCalled)
    }

    @Test
    fun childOnLeafThrowsUOE() {
        val path = "leaf"
        MOCK_DB.update(path, 4)
        assertThrows(UnsupportedOperationException::class.java) {
            MOCK_DB.getThenCall<Any>("$path/any") {}
        }
    }

    @Test
    fun deleteChildOnLeafThrowsUOE() {
        val path = "leaf"
        MOCK_DB.update(path, 4)
        assertThrows(UnsupportedOperationException::class.java) {
            MOCK_DB.delete("$path/any")
        }
    }

    @Test
    fun puttingObjectThrowsUOE() {
        assertThrows(UnsupportedOperationException::class.java) {
            MOCK_DB.update("complexObject", Pair(0,0))
        }
    }

    @Test
    fun removeAbsentListenerWorks() {
        val ref = "ref"
        MOCK_DB.update(ref, 0)
        MOCK_DB.removeListener<Any>(ref) {}
    }

    @Test
    fun childInListWithTooHighIndexIsNull() {
        val ref = "ref"
        MOCK_DB.update(ref,listOf<Any>())
        var callbackCalled = false
        MOCK_DB.getThenCall<Any>("$ref/0") {
            assertNull(it)
            callbackCalled = true
        }
        assertEquals(true, callbackCalled)
    }

    @Test
    fun updateWithEmptyRefUpdatesRoot() {
        MOCK_DB.update("something",45)
        MOCK_DB.update("", mapOf<String,Any>())
        var callbackCalled = false
        MOCK_DB.getThenCall<Map<String,Any>>("") {
            checkMapWithPrimitiveContents(mapOf(),it)
            callbackCalled = true
        }
        assertEquals(true, callbackCalled)
    }

    @Test
    fun addingAndRemovingListenerToNonExistentReferenceDoesNotFail() {
        val ref = "ref"
        MOCK_DB.addListener<Any>(ref) {}
        MOCK_DB.removeListener<Any>(ref) {}
    }

    @Test
    fun transactionDoneCorrectly() {
        val oldValue = "old value"
        val newValue = "new value"
        var checkPointCount = 0
        val speBuilder = TransactionSpecification.Builder<Any> {
            assertEquals(1,checkPointCount)
            checkPointCount += 1
            newValue
        }

        speBuilder.preCheck = {
            assertEquals(0,checkPointCount)
            checkPointCount += 1
            true
        }

        speBuilder.onCompleteCallback = {
            assertEquals(2,checkPointCount)
            checkPointCount += 1
        }

        val ref = "ref"
        var callbackCalled = false
        MOCK_DB.update(ref, oldValue)
        MOCK_DB.runTransaction(ref,speBuilder.build())
        MOCK_DB.getThenCall<String>(ref) {
            assertEquals(newValue,it)
            callbackCalled = true
        }
        assertTrue(callbackCalled)
    }

    @Test
    fun transactionValueNotUpdatedIfPrecheckFails() {
        val ref = "ref"
        val oldValue = "good"
        val speBuilder = TransactionSpecification.Builder<String> { "bad" }

        speBuilder.preCheck = { false }
        assertFalse(speBuilder.preCheck(null))

        var onCompletedCalled = false

        speBuilder.onCompleteCallback = {
            assertEquals(false, it)
            onCompletedCalled = true
        }

        MOCK_DB.update(ref, oldValue)
        MOCK_DB.runTransaction(ref, speBuilder.build())
        assertEquals(true, onCompletedCalled)

        var callbackCalled = false
        MOCK_DB.getThenCall<String>(ref) {
            assertEquals(oldValue, it)
            callbackCalled = true
        }
        assertEquals(true, callbackCalled)

    }

    @Test
    fun defaultTransactionSpecificationBuilderIsCorrect() {
        val speBuilder = TransactionSpecification.Builder<Unit> {}
        speBuilder.onCompleteCallback(true)
        assertTrue(speBuilder.preCheck(null))
    }

    @Test
    fun pinpointsRetry() {
        val path =  "this/is/a/long/path"
        val listOflists = listOf(listOf(0.0,0.0))
        val newListOflists = listOf(listOf(0.0,0.0), listOf(1.0,48.33), listOf(5.0,-8.2), listOf(7.0,-7.0))
        MOCK_DB.update(path,listOflists)

        var listenerCalled = false
        val listener = Listener<List<List<Double>>?> {
            listenerCalled = true
        }
        MOCK_DB.addListener(path, listener)
        MOCK_DB.update(path, newListOflists)
        assertTrue(listenerCalled)

        var callbackCalled = false
        MOCK_DB.getThenCall<List<List<Double>>>(path) {
            assertEquals(newListOflists.size,it?.size)
            for(i in newListOflists.indices) {
                checkListWithPrimitiveContents(newListOflists[i],it?.get(i))
                callbackCalled = true
            }
        }

        assertTrue(callbackCalled)
    }
}