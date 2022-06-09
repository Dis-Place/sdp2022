package com.github.displace.sdp2022.database

import android.content.Intent
import com.github.displace.sdp2022.database.DatabaseConstants.DB_URL

/**
 * to instantiate a Database
 *
 * @see GoodDB
 */
object DatabaseFactory {

    /**
     * use this instance to fill the mock database in tests
     * don't forget to clear it after tests
     * @see clearMockDB
     */
    var MOCK_DB : GoodDB = MockDB()

    const val MOCK_DB_EXTRA_ID = "MOCK_DB"
    const val DEBUG_EXTRA_ID = "DEBUG"


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
     * clear the mock database contents
     */
    fun clearMockDB() {
        MOCK_DB = MockDB()
    }
}