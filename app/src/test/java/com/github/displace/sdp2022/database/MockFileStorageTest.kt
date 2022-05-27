package com.github.displace.sdp2022.database

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File


class MockFileStorageTest {

    private val content = "unchanging file content"
    private val path = "mockFile"
    private val destPath = "dest"

    private lateinit var file : File

    @Before
    fun setupMock() {
        if(::file.isInitialized && file.exists()) {
            file.delete()
        }
        file = File(path)
        file.writeText(content)
        FileStorageFactory.setupMock(file)
    }

    @Test
    fun getFileIsSuccessfulOnWritableDestination() {
        var successCallBackCalled = false
        var failureCallBackCalled = false

        val destination = File("dest")
        FileStorageFactory.mockFileStorage.getThenCall(destination,
            onSuccess = {
                successCallBackCalled = true
                assertEquals(file.readBytes().asList(),destination.readBytes().asList())
            },
            onFailure = {
                failureCallBackCalled = true
            })
        assertTrue(successCallBackCalled)
        assertFalse(failureCallBackCalled)
    }

    /* commented the following test for cirrus, but it works locally
    @Test
    fun getFileFailsOnReadOnlyDestination() {
        var successCallBackCalled = false
        var failureCallBackCalled = false

        val destination = File(destPath)
        destination.setWritable(false)
        FileStorageFactory.mockFileStorage.getThenCall(destination,
            onSuccess = {
                successCallBackCalled = true
            },
            onFailure = {
                failureCallBackCalled = true
            })
        assertFalse(successCallBackCalled)
        assertTrue(failureCallBackCalled)
        destination.setWritable(true)
    }
     */
}