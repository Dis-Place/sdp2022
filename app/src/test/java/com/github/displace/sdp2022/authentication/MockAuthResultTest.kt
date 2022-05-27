package com.github.displace.sdp2022.authentication

import android.os.Parcel
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.lang.UnsupportedOperationException

class MockAuthResultTest {

    val mockAuthResult = MockAuthResult()

    @Test
    fun getAdditionalUserInfoFailsWithUOE() {
        assertThrows(UnsupportedOperationException::class.java) {
            mockAuthResult.additionalUserInfo
        }
    }

    @Test
    fun getCredentialFailsWithUOE() {
        assertThrows(UnsupportedOperationException::class.java) {
            mockAuthResult.credential
        }
    }

    @Test
    fun getUserFailsWithUOE() {
        assertThrows(UnsupportedOperationException::class.java) {
            mockAuthResult.user
        }
    }

    @Test
    fun writeToParcelFailsWithUOE() {
        assertThrows(UnsupportedOperationException::class.java) {
            mockAuthResult.writeToParcel(Parcel.obtain(),0)
        }
    }

    @Test
    fun describeContentsFailsWithUOE() {
        assertThrows(UnsupportedOperationException::class.java) {
            mockAuthResult.describeContents()
        }
    }

    @Test
    fun createFromParcelFailsWithUOE() {
        assertThrows(UnsupportedOperationException::class.java) {
            MockAuthResult.CREATOR.createFromParcel(Parcel.obtain())
        }
    }

    @Test
    fun newArrayWithUOE() {
        assertThrows(UnsupportedOperationException::class.java) {
            MockAuthResult.CREATOR.newArray(0)
        }
    }
}