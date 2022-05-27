package com.github.displace.sdp2022.database

import android.content.Intent
import java.io.File
import java.lang.UnsupportedOperationException

/**
 * to retrieve a FileStorage (mocked or firebase file bucket)
 *
 * @author LeoLgdr
 */
object FileStorageFactory {
    const val MOCK_FILE_STORAGE_EXTRA_ID = "MOCK_FS"

    lateinit var mockFileStorage: FileStorage
        private set

    /**
     * get firebase storage bucket for the given fileUrl,
     * or the mock fileStorage if specified by intent
     *
     * @param intent
     * @param fileUrl
     * @throws UnsupportedOperationException if intent specifies mock, and setupMock() has not
     * been called prior to getFileStorage
     * @return file storage
     */
    fun getFileStorage(intent: Intent, fileUrl: String): FileStorage {
        if(intent.hasExtra(MOCK_FILE_STORAGE_EXTRA_ID)) {
            if(!::mockFileStorage.isInitialized) {
                throw UnsupportedOperationException("mock file storage should be set up before usage")
            }
            return mockFileStorage
        } else {
            return FireStorageReferenceAdapter(fileUrl)
        }
    }

    /**
     * set up the mock file storage
     *
     * @param file content of the mock
     */
    fun setupMock(file: File) {
        mockFileStorage = MockFileStorage(file)
    }
}