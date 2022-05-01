package com.github.displace.sdp2022.database

import android.content.Intent

/**
 * to instantiate a Database
 *
 * @author LeoLgdr
 * @see GoodDB
 */
object DatabaseFactory {

    /**
     * use this instance to fill the mock database in tests
     * don't forget to clear it after tests
     * @see clearMockDB
     */
    val MOCK_DB : GoodDB = MockDB()

    const val MOCK_DB_EXTRA_ID = "MOCK_DB"
    const val DEBUG_EXTRA_ID = "DEBUG"

    private const val DB_URL = "https://displace-dd51e-default-rtdb.europe-west1.firebasedatabase.app/"
    private const val IMAGE_DB_url = "gs://displace-dd51e.appspot.com/"


    private fun get(url: String, intent: Intent): GoodDB {
        if(intent.hasExtra(MOCK_DB_EXTRA_ID)) {
            return MOCK_DB
        }
        return FirebaseDatabaseAdapter(url, intent.hasExtra(DEBUG_EXTRA_ID))
    }

    /**
     * @param intent specifies if the database should be mocked and/or in debug mode or not
     * @return database instance
     */
    fun getDB(intent: Intent): GoodDB {
        return get(DB_URL, intent)
    }

    /**
     * @param intent specifies if the database should be mocked and/or in debug mode or not
     * @return image database instance
     */
    fun getImageDB(intent: Intent): GoodDB {
        return get(IMAGE_DB_url, intent)
    }

    /**
     * clear the mock database contents
     */
    fun clearMockDB() {
        (MOCK_DB as MockDB).clear()
    }
}