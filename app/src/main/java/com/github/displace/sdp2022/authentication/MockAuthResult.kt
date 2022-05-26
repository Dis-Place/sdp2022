package com.github.displace.sdp2022.authentication

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.lang.UnsupportedOperationException

/**
 * Mock AuthResult to comply
 *
 */
class MockAuthResult : AuthResult {

    override fun getAdditionalUserInfo(): Nothing = throw UnsupportedOperationException()

    override fun getCredential(): Nothing = throw UnsupportedOperationException()

    override fun getUser(): Nothing = throw UnsupportedOperationException()

    /* to comply with parcelability of AuthResult
    note : should not be used */
    companion object CREATOR : Parcelable.Creator<MockAuthResult> {
        override fun createFromParcel(parcel: Parcel): Nothing = throw UnsupportedOperationException()

        override fun newArray(size: Int): Nothing = throw UnsupportedOperationException()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) : Nothing = throw UnsupportedOperationException()

    override fun describeContents(): Nothing = throw UnsupportedOperationException()

}